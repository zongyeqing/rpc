package netty.rpc.core.server;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import netty.rpc.core.protocal.MessageRequest;
import netty.rpc.core.protocal.MessageResponse;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


/**
 * 请填写类注释
 *
 * @author 宗业清 
 * @since 2018年03月23日
 */
public class MessageRecvInitializeTask implements Runnable {
    
    private MessageRequest request = null;
    private MessageResponse response = null;
    private ChannelHandlerContext ctx = null;
    private static ApplicationContext applicationContext;

    public MessageResponse getResponse() {
        return response;
    }

    public MessageRequest getRequest() {
        return request;
    }
    
    public void setRequest(MessageRequest request) {
        this.request = request;
    }
    
    MessageRecvInitializeTask(MessageRequest request, MessageResponse response, ChannelHandlerContext ctx) {
        this.request = request;
        this.response = response;
        this.ctx = ctx;
    }

    public void run() {
        
        response.setMessageId(request.getMessageId());
        try {
            Object result = reflect(request);
            response.setResult(result);
        }catch (Throwable t) {
            response.setError(t.toString());
            t.printStackTrace();
            System.err.println("RPC Server invoke error\n");
        }
        
        ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                System.out.println("RPC Server send message-id response: " + request.getMessageId());
            }
        });
        
    }
    
    private Object reflect(MessageRequest request) throws Throwable {
        String className = request.getClassName();
        Class interfaceClass = Class.forName(className);
        Object serviceBean = applicationContext.getBean(interfaceClass);
        String methodName = request.getMethodName();
        Object[] parameters = request.getParametersVal();
        return MethodUtils.invokeMethod(serviceBean, methodName, parameters);
    }
    
    public static void setApplicationContext(ApplicationContext ctx) {
        applicationContext = ctx;
    }
}
