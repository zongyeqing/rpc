package netty.rpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 远程服务接口的注解，系统启动时会扫描规定目录下的含有RemoteService的接口，
 * 然后将此接口和远程代理类注册到springBeanFactory中
 *
 * @author 宗业清 
 * @since 2018年03月29日
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RemoteService {
    String value() default "";
}
