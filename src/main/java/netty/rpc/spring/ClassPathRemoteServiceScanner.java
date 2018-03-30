package netty.rpc.spring;

import netty.rpc.annotation.RemoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.env.Environment;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Set;

/**
 * 请填写类注释
 *
 * @author 宗业清
 * @since 2018年03月28日
 */
public class ClassPathRemoteServiceScanner extends ClassPathBeanDefinitionScanner {

    /** 日志记录器 */
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassPathRemoteServiceScanner.class);
    
    private String excludePackage;
    
    private Class<? extends Annotation> annotationClass;

    private Class<?> markerInterface;

    private RemoteServiceProxyFactoryBean<Object> exportServiceProxyFactoryBean = new RemoteServiceProxyFactoryBean<Object>();
    
    public ClassPathRemoteServiceScanner(BeanDefinitionRegistry registry, String excludePackage, Environment environment) {
        super(registry, true, environment);
        this.excludePackage = excludePackage;
    }

    public ClassPathRemoteServiceScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters) {
        super(registry, useDefaultFilters);
    }

    public ClassPathRemoteServiceScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters, Environment environment) {
        super(registry, useDefaultFilters, environment);
    }

    public void setAnnotationClass(Class<? extends Annotation> annotationClass) {
        this.annotationClass = annotationClass;
    }
    
    public void registerFilters() {
        boolean accpetAllInterfaces = true;
        if (annotationClass != null) {
            addIncludeFilter(new AnnotationTypeFilter(annotationClass));
            accpetAllInterfaces = false;
        }
        if (this.markerInterface != null) {
            addIncludeFilter(new AssignableTypeFilter(this.markerInterface) {
                @Override
                protected boolean matchClassName(String className) {
                    return false;
                }
            });
            accpetAllInterfaces = false;
        }
        if (accpetAllInterfaces) {
            addIncludeFilter(new TypeFilter() {
                @Override
                public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
                    return true;
                }
            });
        }
        addExcludeFilter(new TypeFilter() {
            @Override
            public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
                String className = metadataReader.getClassMetadata().getClassName();
                if (excludePackage != null && className.startsWith(excludePackage)) {
                    return true;
                }

                return className.endsWith("package-info");
            }
        });
    }

    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitionHolders =  super.doScan(basePackages);
        if(beanDefinitionHolders.isEmpty()) {
            LOGGER.warn("No ExportService interface was found in '" + Arrays.toString(basePackages) + "' package. Please check your configuration.");
        } else {
            processBeanDefinitions(beanDefinitionHolders);
        }
        return beanDefinitionHolders;
    }
    
    private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitionHolders) {
        GenericBeanDefinition definition;
        for (BeanDefinitionHolder holder : beanDefinitionHolders) {
            definition = (GenericBeanDefinition) holder.getBeanDefinition();
            
            LOGGER.info("Creating ExportServiceProxyFactoryBean with name '" + holder.getBeanName()
                        + "' and '" + definition.getBeanClassName() + "' mapperInterface");
            
            try {
                Class<?> clazz = definition.resolveBeanClass(Thread.currentThread().getContextClassLoader());
                RemoteService annotation = clazz.getAnnotation(RemoteService.class);
                if (annotation != null) {
                    definition.getConstructorArgumentValues().addGenericArgumentValue(definition.getBeanClass());
                    definition.setBeanClass(this.exportServiceProxyFactoryBean.getClass());
                    definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
                }
            }catch (ClassNotFoundException e) {
                //ignore
                LOGGER.error("", e);
            }
        }
    }

    @Override
    protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition) throws IllegalStateException {
        if (super.checkCandidate(beanName, beanDefinition)) {
            return true;
        } else {
            LOGGER.warn("Skipping MapperFactoryBean with name '" + beanName
                     + "' and '" + beanDefinition.getBeanClassName() + "' mapperInterface"
                     + ". Bean already defined with the same name!");
            return false;
        }
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
    }
}
