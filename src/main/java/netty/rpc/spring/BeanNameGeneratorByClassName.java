package netty.rpc.spring;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;

/**
 * 请填写类注释
 *
 * @author 宗业清 
 * @since 2018年03月30日
 */
public class BeanNameGeneratorByClassName implements BeanNameGenerator {
    
    @Override
    public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
        return definition.getBeanClassName();
    }
}
