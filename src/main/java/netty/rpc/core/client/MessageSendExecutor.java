package netty.rpc.core.client;

import java.lang.reflect.Proxy;

/**
 * 请填写类注释
 *
 * @author 宗业清 yeqing.zong@ucarinc.com
 * @since 2018年03月30日
 */
public class MessageSendExecutor {
    
    private RpcServerLoader loader = new RpcServerLoader();
    
    public MessageSendExecutor(String serverAddress) {
        loader.load(serverAddress);
    }
    
    public void stop(){
        loader.unLoad();
    }
    
    public <T> T execute(Class<T> rpcInterface) {
        return (T) Proxy.newProxyInstance(rpcInterface.getClassLoader(), new Class<?>[]{rpcInterface}, new MessageSendProxy<T>(rpcInterface));
    }
}
