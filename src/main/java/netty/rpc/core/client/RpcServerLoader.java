package netty.rpc.core.client;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import netty.rpc.core.serialize.RpcSerializeProtocol;
import netty.rpc.core.server.RpcThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 请填写类注释
 *
 * @author 宗业清 
 * @since 2018年03月30日
 */
public class RpcServerLoader {
    
    private volatile static RpcServerLoader rpcServerLoader;
    private final static String DELIMITER = ":";
    private RpcSerializeProtocol seriabizedProtocol = RpcSerializeProtocol.JDKSERIALIZE;
    
    /** netty nio线程池 */
    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(1);
    private static ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) RpcThreadPool.getExecutor(1, -1);
    private MessageSendHandler messageSendHandler = null;
    
    /** 等待netty服务端链路建立通知信号 */
    private Lock lock = new ReentrantLock();
    private Condition signal = lock.newCondition();
    
    public RpcServerLoader(){
    }
    
    @SuppressWarnings("unchecked")
    public MessageSendHandler load(String serverAddress) {
        String[] ipAddr = serverAddress.split(RpcServerLoader.DELIMITER);
        if (ipAddr.length == 2) {
            String host = ipAddr[0];
            int port = Integer.parseInt(ipAddr[1]);
            final InetSocketAddress remoteAddr = new InetSocketAddress(host, port);
            MessageSendInitializeTask messageSendInitializeTask = new MessageSendInitializeTask(eventLoopGroup, remoteAddr, this, seriabizedProtocol);
            Future<Object> future =  threadPoolExecutor.submit(messageSendInitializeTask);
            try {
                return (MessageSendHandler)future.get();
            } catch (Exception e) {
                throw new RuntimeException("获取Channel通道错误", e);
            }
        }
        return null;
    }
    
    public void setMessageSendHandler(MessageSendHandler messageSendHandler) {
        try {
            lock.lock();
            this.messageSendHandler = messageSendHandler;
            //唤醒所有等待客户端RPC线程
            signal.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public MessageSendHandler getMessageSendHandler() throws InterruptedException {
        try{
            lock.lock();
            //netty服务端链路没有建立完毕之前，先挂起等待
             if (messageSendHandler == null) {
                 signal.await();
             }
             return messageSendHandler;
        } finally {
            lock.unlock();
        }
    }

    public void unLoad() {
        messageSendHandler.close();
        threadPoolExecutor.shutdown();
        eventLoopGroup.shutdownGracefully();
    }

    public void setSeriabizedProtocol(RpcSerializeProtocol seriabizedProtocol) {
        this.seriabizedProtocol = seriabizedProtocol;
    }
    
}
