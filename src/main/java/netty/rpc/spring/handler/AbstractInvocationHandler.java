package netty.rpc.spring.handler;

import netty.rpc.core.client.MessageCallback;
import netty.rpc.core.client.MessageSendExecutor;
import netty.rpc.core.client.MessageSendHandler;
import netty.rpc.core.client.RpcLoaderManager;
import netty.rpc.core.client.RpcServerLoader;
import netty.rpc.core.protocal.MessageRequest;
import netty.rpc.remote.RemoteServiceManage;
import netty.rpc.remote.ServiceMeta;
import netty.rpc.remote.ServiceNameGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 请填写类注释
 *
 * @author 宗业清
 * @since 2018年03月28日
 */
public abstract class AbstractInvocationHandler implements InvocationHandler{
    /** 日志记录器 */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractInvocationHandler.class);
    
    private static final Map<Class<?>, Object> primitiveValue = new HashMap<>();
    
    static {
        primitiveValue.put(Boolean.TYPE, false);
        primitiveValue.put(Byte.TYPE, 0);
        primitiveValue.put(Character.TYPE, '\u0000');
        primitiveValue.put(Short.TYPE, 0);
        primitiveValue.put(Integer.TYPE, 0);
        primitiveValue.put(Long.TYPE, 0L);
        primitiveValue.put(Double.TYPE, 0.0d);
        primitiveValue.put(Float.TYPE, 0.0f);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        long startTime = System.currentTimeMillis();
        //根据接口生成服务名称
        String serviceName = ServiceNameGenerator.generate(method);
        //随机选择服务
        ServiceMeta serviceMeta = RemoteServiceManage.getInstance().getService(new ServiceMeta(serviceName));
        //使用该service执行
        MessageSendHandler messageSendHandler = RpcLoaderManager.getMessageSendHandler(serviceMeta.getAddress());
        MessageRequest request = new MessageRequest();
        request.setMessageId(UUID.randomUUID().toString());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setTypeParameters(method.getParameterTypes());
        request.setParametersVal(args);
        MessageCallback callback = messageSendHandler.sendRequest(request);
        return callback.start();
    }
}
