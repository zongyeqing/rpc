package netty.rpc.core.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.util.Map;


/**
 * 请填写类注释
 *
 * @author 宗业清
 * @since 2018年03月22日
 */
public class MessageRecvChannelInitializer extends ChannelInitializer<SocketChannel> {
    
    
    /**
     *  ObjectDecoder 底层默认继承半包解码器LengthFieldBasedFrameDecoder处理粘包问题的时候
     *  消息头开始即为长度字段，占据4个字节。这里处于保守兼容的考虑
     */
    public final static int MESSAGE_LENGTH = 4;
    
    
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        // ObjectDecoder的基类半包解码器LengthFieldBasedFrameDecoder的报文格式保持兼容。因为底层的父类
        // LengthFieldBasedFrameDecoder 的初始化参数即为super(maxObjectSize, 0, 4, 0, 4);
        pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, MessageRecvChannelInitializer.MESSAGE_LENGTH, 0, MessageRecvChannelInitializer.MESSAGE_LENGTH));
        // 利用LengthFieldPrepender 回填补充ObjectDecoder消息报文头
        pipeline.addLast(new LengthFieldPrepender(MessageRecvChannelInitializer.MESSAGE_LENGTH));
        pipeline.addLast(new ObjectEncoder());
        //考虑到并发性能，采用weakCachingConcurrentResolver缓存策略。一般情况使用:cacheDisabled即可
        pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
        pipeline.addLast(new MessageRecvHandler());
    }
}
