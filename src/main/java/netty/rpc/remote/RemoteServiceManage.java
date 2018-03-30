package netty.rpc.remote;


import netty.rpc.annotation.RemoteService;
import netty.rpc.exception.FrameworkException;
import netty.rpc.zookeeper.Listener;
import netty.rpc.zookeeper.ZKClient;
import netty.rpc.zookeeper.chooser.Chooser;
import netty.rpc.zookeeper.chooser.RandomChooser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Servers管理
 *
 * @author 宗业清
 * @since 2018年03月29日
 */
public class RemoteServiceManage {
    
    /** 日志记录器 */
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteServiceManage.class);

    private static RemoteServiceManage remoteServiceManage;

    private Map<String, List<ServiceMeta>> serviceMap = new ConcurrentHashMap<String, List<ServiceMeta>>();

    private static String apiBathPath;

    private volatile boolean isLoaded = false;

    static {
        try {
            Properties properties = new Properties();
            properties.load(RemoteServiceManage.class.getResourceAsStream("/config.properties"));
            apiBathPath = properties.getProperty("apiBasePath");
            if(apiBathPath == null) {
                apiBathPath = "#";
            }
        } catch (IOException e) {
            apiBathPath = "#";
            LOGGER.error("加载config.properties出错", e);
        }
    }

    private RemoteServiceManage() {

    }

    public static RemoteServiceManage getInstance() {
        if(remoteServiceManage == null) {
            synchronized(RemoteServiceManage.class) {
                if(remoteServiceManage == null) {
                    remoteServiceManage = new RemoteServiceManage();
                }
            }
        }
        return remoteServiceManage;
    }

    /**
     * 从zookeeper上重新拉取数据刷新本地serviceMap
     */
    public void refresh() {
        if(!CollectionUtils.isEmpty(this.serviceMap)) {
            this.serviceMap.clear();
        }
        List<ServiceMeta> serviceMetas = ZKClient.dicoverAll(new Listener() {
            @Override
            public void onChange(String parentPath, List<String> list) {
                refresh();
            }
        });
        for(ServiceMeta serviceMeta : serviceMetas) {
            String name = serviceMeta.getName();
            if(!this.serviceMap.containsKey(name)) {
                this.serviceMap.put(name, new ArrayList<ServiceMeta>());
            }
            List<ServiceMeta> serviceMetaList = this.serviceMap.get(name);
            serviceMetaList.add(serviceMeta);
        }
        this.isLoaded = true;
    }

    /**
     * 默认采用随机选择服务
     *
     * @param serviceMeta serviceMeta
     * @return 远程服务信息
     */
    public ServiceMeta getService(ServiceMeta serviceMeta) {
        return getService(serviceMeta, new RandomChooser());
    }

    /**
     * 使用选择器选择服务
     *
     * @param serviceMeta serviceMata
     * @param chooser     选择器
     * @return 远程服务信息
     */
    public ServiceMeta getService(ServiceMeta serviceMeta, Chooser chooser) {
        List<ServiceMeta> serviceMetas = this.getServices(serviceMeta);
        ServiceMeta service =  chooser.choose(serviceMetas);
        service.setAddress(ZKClient.getServicePath(service));
        return service;
    }

    private List<ServiceMeta> getServices(ServiceMeta serviceMeta) {
        Assert.notNull(serviceMeta);
        Assert.notNull(serviceMeta.getName());
        if(!isLoaded) {
            synchronized(this) {
                if(!isLoaded) {
                    this.refresh();
                }
            }
        }
        List<ServiceMeta> serviceMetas = this.serviceMap.get(serviceMeta.getName());
        if(CollectionUtils.isEmpty(serviceMap)) {
            throw new FrameworkException("没有查找到此服务:" + serviceMeta.getName());
        }
        return serviceMetas;
    }

    public void register(ApplicationContext applicationContext) {
        Properties properties = null;
        try {
            properties = PropertiesLoaderUtils.loadAllProperties("netty.properties");
        } catch (IOException e) {
            throw new FrameworkException("没有netty.properties文件");
        }
        String addr = properties.getProperty("rpc.server.addr");
        if(StringUtils.isEmpty(addr)) {
            throw new FrameworkException("netty配置文件中没有配置rpc.server.addr");
        }
        Map<String, Object> beanMap = applicationContext.getBeansWithAnnotation(RemoteService.class);
        if(!CollectionUtils.isEmpty(beanMap)) {
            for(Map.Entry<String, Object> entry : beanMap.entrySet()) {
                String serviceName = entry.getKey();
                if(serviceName.startsWith(apiBathPath)) {
                    ZKClient.register(new ServiceMeta(serviceName, addr));
                }
            }
        }
    }

}
