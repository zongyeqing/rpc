package netty.rpc.core.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import netty.rpc.core.protocal.MessageRequest;
import netty.rpc.core.protocal.MessageResponse;


/**
 * 请填写类注释
 *
 * @author 宗业清 
 * @since 2018年03月23日
 */
public class MessageRecvHandler extends SimpleChannelInboundHandler {
    
   
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
        MessageRequest request = (MessageRequest) msg;
        MessageResponse response = new MessageResponse();
        MessageRecvInitializeTask recvTask = new MessageRecvInitializeTask(request, response, ctx);
        // 不要阻塞nio线程，复杂的业务逻辑丢给专门的线程池
        MessageRecvExecutor.sumbit(recvTask);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //网络有异常要关闭通道
        ctx.close();
    }
}
