package netty.rpc.core.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import netty.rpc.exception.FrameworkException;
import netty.rpc.remote.RemoteServiceManage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.nio.channels.spi.SelectorProvider;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * netty的启动类
 *
 * @author 宗业清 
 * @since 2018年03月29日
 */
public class NettyStart implements ApplicationContextAware {
    
    /** 日志记录器 */
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyStart.class);
    private ApplicationContext applicationContext;
    
    public void start() {
        //netty的线程池模型设置成主从线程池模式，这样可以应对高并发请求
        ThreadFactory threadFactory = new NamedThreadFactory("NettyRPC ThreadFactory");
        
        //java虚拟机可用的处理器数量
        int parallel = Runtime.getRuntime().availableProcessors() * 2;
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup(parallel, threadFactory, SelectorProvider.provider());
        
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, worker).channel(NioServerSocketChannel.class)
            .childHandler(new MessageRecvChannelInitializer())
            .option(ChannelOption.SO_BACKLOG,128)
            .childOption(ChannelOption.SO_KEEPALIVE, true);
            //将本机可用的服务注册到服务管理中心
            RemoteServiceManage.getInstance().register(this.applicationContext);
            MessageRecvInitializeTask.setApplicationContext(this.applicationContext);

            Properties properties = null;
            try {
                properties = PropertiesLoaderUtils.loadAllProperties("netty.properties");
            } catch (IOException e) {
                throw new FrameworkException("没有netty.properties文件");
            }
            String addr = properties.getProperty("rpc.server.addr");
            if (StringUtils.isEmpty(addr)) {
                throw new FrameworkException("netty配置文件中没有配置rpc.server.addr");
            }
            String[] ipAddr = addr.split(MessageRecvExecutor.DELIMITER);

            if(ipAddr.length == 2) {
                String host = ipAddr[0];
                int port = Integer.parseInt(ipAddr[1]);
                ChannelFuture future = bootstrap.bind(host, port).sync();
                System.out.printf("Netty RPC Server start success ip:%s port:%d\n", host, port);
                future.channel().closeFuture().sync();
            } else {
                LOGGER.error("Netty RPC Server start fail!\n");
            }
        } catch (InterruptedException e) {
            LOGGER.error("rpc退出");
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                start();
            }
        });
    }
}
