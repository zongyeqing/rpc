package netty.rpc;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 请填写类注释
 *
 * @author 宗业清 
 * @since 2018年03月29日
 */
public class Start {

    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("config.xml");
    }
}
