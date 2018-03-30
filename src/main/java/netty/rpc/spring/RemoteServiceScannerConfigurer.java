package netty.rpc.spring;

import netty.rpc.annotation.RemoteService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultBeanNameGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * 请填写类注释
 *
 * @author 宗业清
 * @since 2018年03月28日
 */
public class RemoteServiceScannerConfigurer implements BeanDefinitionRegistryPostProcessor,InitializingBean,ApplicationContextAware {
    
    private ApplicationContext applicationContext;
    
    private String basePackage;
    
    private String excludePackage;

    /** 使用全名生成spring bean, 避免冲突 */
    private BeanNameGenerator nameGenerator = new BeanNameGeneratorByClassName();
    
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
       ClassPathRemoteServiceScanner scanner = new ClassPathRemoteServiceScanner(registry, excludePackage, applicationContext.getEnvironment());
       scanner.setAnnotationClass(RemoteService.class);
       scanner.setResourceLoader(applicationContext);
       scanner.setBeanNameGenerator(nameGenerator);
       scanner.registerFilters();
       
       scanner.scan(StringUtils.tokenizeToStringArray(basePackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.basePackage, "Property 'basePackage' is required");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;   
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public void setExcludePackage(String excludePackage) {
        this.excludePackage = excludePackage;
    }
}
