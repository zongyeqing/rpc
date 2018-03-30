package netty.rpc.core.server;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * rpc线程池封装
 *
 * @author 宗业清 
 * @since 2017年09月27日
 */
public class RpcThreadPool {

    public static Executor getExecutor(int threads, int queues){
        String name = "RpcThreadPool";
        return new ThreadPoolExecutor(threads, threads, 0, TimeUnit.MILLISECONDS,
        queues == 0 ? new SynchronousQueue<Runnable>() :
        (queues < 0 ? new LinkedBlockingQueue<Runnable>() : new LinkedBlockingQueue<Runnable>(queues)),
        new NamedThreadFactory(name, true), new AbortPolicyWithReport(name));
    }
}
