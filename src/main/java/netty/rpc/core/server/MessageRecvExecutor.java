package netty.rpc.core.server;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 请填写类注释
 *
 * @author 宗业清
 * @since 2018年03月22日
 */
public class MessageRecvExecutor {

    private String serverAddress;
    public final static String DELIMITER = ":";

    private Map<String, Object> handlerMap = new ConcurrentHashMap<String, Object>();

    private static ThreadPoolExecutor threadPoolExecutor;

    public MessageRecvExecutor(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public static void sumbit(Runnable task) {
        if(threadPoolExecutor == null) {
            synchronized(MessageRecvExecutor.class) {
                if(threadPoolExecutor == null) {
                    threadPoolExecutor = (ThreadPoolExecutor) RpcThreadPool.getExecutor(16, -1);
                }
            }
        }
        threadPoolExecutor.submit(task);
    }
}
