package netty.rpc.core.client;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import netty.rpc.core.protocal.MessageRequest;
import netty.rpc.core.protocal.MessageResponse;

import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 请填写类注释
 *
 * @author 宗业清 
 * @since 2018年03月29日
 */
public class MessageSendHandler extends ChannelInboundHandlerAdapter {
    
    private Map<String, MessageCallback> mapCallBack = new ConcurrentHashMap<>();
    
    private volatile Channel channel;
    
    private SocketAddress remoteAddr;

    public Channel getChannel() {
        return channel;
    }

    public SocketAddress getRemoteAddr() {
        return remoteAddr;
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.remoteAddr = this.channel.remoteAddress();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MessageResponse response = (MessageResponse) msg;
        String messageId = response.getMessageId();
        MessageCallback callback = mapCallBack.get(messageId);
        if (callback != null) {
            mapCallBack.remove(messageId);
            callback.over(response);
        }
    }

    public void close() {
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }
    
    public MessageCallback sendRequest(MessageRequest request) {
        MessageCallback callback = new MessageCallback(request);
        mapCallBack.put(request.getMessageId(), callback);
        channel.writeAndFlush(request);
        return callback;
    }
}
