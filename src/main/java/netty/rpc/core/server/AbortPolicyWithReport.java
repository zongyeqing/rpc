package netty.rpc.core.server;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池异常策略
 *
 * @author 宗业清
 * @since 2017年09月27日
 */
public class AbortPolicyWithReport extends ThreadPoolExecutor.AbortPolicy {
    private final String threadName;

    public AbortPolicyWithReport(String threadName){
        this.threadName = threadName;
    }

    public void rejectExecution(Runnable runnable, ThreadPoolExecutor executor){
        String msg = String.format("RpcServer{" + "Thread Name : %s, Pool Size : %d (active : %d, core : %d, max: %d, largest: %d) Task: %d (completed: %d),"
            + " Executor status:(isShutdown:%s, isTerminated:%s, isTerminating:%s)]",
            threadName, executor.getPoolSize(), executor.getActiveCount(), executor.getCorePoolSize(), executor.getMaximumPoolSize(),
            executor.getLargestPoolSize(),executor.getTaskCount(), executor.getCompletedTaskCount(), executor.isShutdown(),
            executor.isTerminated(), executor.isTerminating());
        System.out.println(msg);
        throw new RejectedExecutionException(msg);
    }
}
