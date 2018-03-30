package netty.rpc.core.server;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 请填写类注释
 *
 * @author 宗业清 
 * @since 2018年03月29日
 */
public class NamedThreadFactory implements ThreadFactory {
    
    private static final AtomicInteger threadNumber = new AtomicInteger(1);
    private final AtomicInteger mThreadNum = new AtomicInteger(1);
    private final String prefix;
    private final boolean daemonThred;
    private final ThreadGroup threadGroup;
    
    public NamedThreadFactory() {
        this("rpc-server-threadPool-" + threadNumber.getAndIncrement(), false);
    }
    
    public NamedThreadFactory(String prefix) {
        this(prefix, false);
    }
    
    public NamedThreadFactory(String prefix, boolean daem) {
        this.prefix = prefix + "-thread";
        daemonThred = daem;
        SecurityManager s = System.getSecurityManager();
        threadGroup = (s == null) ? Thread.currentThread().getThreadGroup() : s.getThreadGroup();
    }
    
    public Thread newThread(Runnable r) {
        String name = prefix + mThreadNum.getAndIncrement();
        Thread ret = new Thread(threadGroup, r, name, 0);
        ret.setDaemon(daemonThred);
        return ret;
    }
}
