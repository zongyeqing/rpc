package netty.rpc.core.client;

import netty.rpc.core.protocal.MessageRequest;
import netty.rpc.core.protocal.MessageResponse;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 请填写类注释
 *
 * @author 宗业清 
 * @since 2018年03月29日
 */
public class MessageCallback {
    
    private MessageRequest request;
    private MessageResponse response;
    private Lock lock = new ReentrantLock();
    private Condition finish = lock.newCondition();
    
    public MessageCallback(MessageRequest request) {
        this.request = request;
    }
    
    public Object start() throws InterruptedException {
        try{
            lock.lock();
            finish.await(10 * 1000, TimeUnit.MILLISECONDS);
            if (this.response != null) {
                return this.response.getResult();
            } else {
                return null;
            }
        }finally {
            lock.unlock();
        }
    }
    
    public void over(MessageResponse response) {
        try {
            lock.lock();
            finish.signal();
            this.response = response;
        }finally {
            lock.unlock();
        }
    }
}
