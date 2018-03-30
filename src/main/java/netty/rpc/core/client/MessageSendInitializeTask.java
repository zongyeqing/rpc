package netty.rpc.core.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import netty.rpc.core.serialize.RpcSerializeProtocol;

import java.net.InetSocketAddress;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * 请填写类注释
 *
 * @author 宗业清 yeqing.zong@ucarinc.com
 * @since 2018年03月30日
 */
public class MessageSendInitializeTask implements Callable{

    private EventLoopGroup eventLoopGroup;
    private InetSocketAddress serverAddress;
    private RpcServerLoader loader;
    private RpcSerializeProtocol serializeProtocol;

    MessageSendInitializeTask(EventLoopGroup eventLoopGroup, InetSocketAddress serverAddress, RpcServerLoader loader, RpcSerializeProtocol serializeProtocol) {
        this.eventLoopGroup = eventLoopGroup;
        this.serverAddress =serverAddress;
        this.loader = loader;
        this.serializeProtocol = serializeProtocol;
    }
    

    @Override
    public Object call() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final MessageSendHandlerWrapper handlerWrapper = new MessageSendHandlerWrapper();
        Bootstrap b = new Bootstrap();
        b.group(eventLoopGroup)
        .channel(NioSocketChannel.class).option(ChannelOption.SO_KEEPALIVE, true);
        b.handler(new MessageSendChannelInitializer());
        ChannelFuture channelFuture = b.connect(serverAddress);
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(final ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    MessageSendHandler handler = channelFuture.channel().pipeline().get(MessageSendHandler.class);
                    handlerWrapper.setMessageSendHandler(handler);
                    latch.countDown();
                }
            }
        });
        latch.await();
        return handlerWrapper.getMessageSendHandler();
    }
    
    private class MessageSendHandlerWrapper{
        private MessageSendHandler messageSendHandler;
        private int id;

        public MessageSendHandler getMessageSendHandler() {
            return messageSendHandler;
        }

        public void setMessageSendHandler(MessageSendHandler messageSendHandler) {
            this.messageSendHandler = messageSendHandler;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
