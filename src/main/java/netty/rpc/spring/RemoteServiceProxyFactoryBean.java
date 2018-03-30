package netty.rpc.spring;

import netty.rpc.spring.handler.AbstractInvocationHandler;
import netty.rpc.spring.handler.DefaultInvocationHandler;
import netty.rpc.spring.handler.TestInvocationHandler;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

/**
 * 请填写类注释
 *
 * @author 宗业清
 * @since 2018年03月28日
 */
public class RemoteServiceProxyFactoryBean<T> implements FactoryBean<T> {
    
    private boolean test = false;
    
    private Class<T> exportServiceInterface;

    public RemoteServiceProxyFactoryBean() {
    }
    
    public RemoteServiceProxyFactoryBean(Class<T> exportServiceInterface){
        this.exportServiceInterface = exportServiceInterface;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public T getObject() throws Exception {
        AbstractInvocationHandler invocationHandler;
        if (!test) {
            invocationHandler = new DefaultInvocationHandler();
        } else {
            invocationHandler = new TestInvocationHandler();
        }
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),new Class[]{this.exportServiceInterface}, invocationHandler);
    }

    @Override
    public Class<?> getObjectType() {
        return this.exportServiceInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public Class<T> getExportServiceInterface() {
        return exportServiceInterface;
    }

    public void setTest(boolean test) {
        this.test = test;
    }

    public void setExportServiceInterface(Class<T> exportServiceInterface) {
        this.exportServiceInterface = exportServiceInterface;
    }
}
