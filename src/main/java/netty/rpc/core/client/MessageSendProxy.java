package netty.rpc.core.client;

import netty.rpc.core.protocal.MessageRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * 请填写类注释
 *
 * @author 宗业清 yeqing.zong@ucarinc.com
 * @since 2018年03月30日
 */
public class MessageSendProxy<T> implements InvocationHandler{
    
    private Class<T> cls;
    
    public MessageSendProxy(Class<T> cls) {
        this.cls = cls;
    }
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      /*  MessageRequest request = new MessageRequest();
        request.setMessageId(UUID.randomUUID().toString());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setTypeParameters(method.getParameterTypes());
        request.setParametersVal(args);
        MessageSendHandler handler = RpcServerLoader.getInstance().getMessageSendHandler();
        MessageCallback callback = handler.sendRequest(request);
        return callback.start();*/
      return null;
    }
}
