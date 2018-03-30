package netty.rpc.remote;

import java.lang.reflect.Method;

/**
 * 请填写类注释
 *
 * @author 宗业清 
 * @since 2018年03月30日
 */
public class ServiceNameGenerator {
    
    public static String generate(Method method){
        return method.getDeclaringClass().getName();
    }
}
