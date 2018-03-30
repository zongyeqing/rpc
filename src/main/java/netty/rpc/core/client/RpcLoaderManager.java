package netty.rpc.core.client;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * 请填写类注释
 *
 * @author 宗业清 
 * @since 2018年03月30日
 */
public class RpcLoaderManager {
    
    private static Map<String, RpcServerLoader> loaderMap = new WeakHashMap<>();
    
    public static MessageSendHandler getMessageSendHandler(String address) {
        if (StringUtils.isEmpty(address)) {
            throw new RuntimeException("服务地址不能为空");
        }
        RpcServerLoader serverLoader = loaderMap.get(address);
        if (serverLoader == null) {
            serverLoader = new RpcServerLoader();
            loaderMap.put(address, serverLoader);
        }
        return serverLoader.load(address);
    }
}
