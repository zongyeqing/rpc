package netty.rpc.zookeeper;

import netty.rpc.remote.ServiceMeta;
import netty.rpc.zookeeper.chooser.Chooser;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * zk客户端
 *
 * @author 宗业清
 * @since 2018年03月29日
 */
public class ZKClient {
    /** 日志记录器 */
    private static final Logger LOGGER = LoggerFactory.getLogger(ZKClient.class);
    /** zookeeper地址 */
    private static final String ZOOKEEPER_ADDRESS;
    /** session超时时间 */
    private static Integer SESSION_TIME_OUT = 30000;
    /** connection超时时间 */
    private static Integer CONNECTION_TIME_OUT = 10000;

    private static final ZkClient ZK_CLIENT;

    private static final String SERVICE_ROOT_PATH = "/services";

    static {
        Properties properties = null;
        try {
            properties = PropertiesLoaderUtils.loadAllProperties("zookeeper.properties");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Assert.notEmpty(properties);
        ZOOKEEPER_ADDRESS = properties.getProperty("zkAddress");
        SESSION_TIME_OUT = Integer.parseInt(properties.getProperty("sessionTimeOut"));
        CONNECTION_TIME_OUT = Integer.parseInt(properties.getProperty("connectionTimeOut"));
        ZK_CLIENT = new ZkClient(ZOOKEEPER_ADDRESS, SESSION_TIME_OUT, CONNECTION_TIME_OUT);
    }

    /**
     * 注册服务
     *
     * @param service serviceMeta
     */
    public static void register(ServiceMeta service) {
        Assert.notNull(service, "service can not be null");
        Assert.noNullElements(new Object[]{service.getAddress(), service.getName()}, "service name or address can not be null");
        if(!ZK_CLIENT.exists(SERVICE_ROOT_PATH)) {
            ZK_CLIENT.createPersistent(SERVICE_ROOT_PATH);
        }
        String servicePath = SERVICE_ROOT_PATH + "/" + service.getName();
        if(!ZK_CLIENT.exists(servicePath)) {
            ZK_CLIENT.createPersistent(servicePath);
        }
        String path = ZK_CLIENT.createEphemeralSequential(servicePath + "/address", service.getAddress());
        LOGGER.info("已注册服务: " + path + "address: " + service.getAddress());
    }
    
    public static String getServicePath(ServiceMeta serviceMeta){
        String path = SERVICE_ROOT_PATH + "/" + serviceMeta.getName() + "/" + serviceMeta.getAddress();
        return ZK_CLIENT.readData(path);
    }

    /**
     * 发现服务
     *
     * @param serviceMeta serviceMeta
     */
    public static List<ServiceMeta> discover(ServiceMeta serviceMeta) {
        Assert.notNull(serviceMeta);
        Assert.notNull(serviceMeta.getName());
        List<ServiceMeta> serviceMetas = new ArrayList<ServiceMeta>();
        String servicePath = SERVICE_ROOT_PATH + "/" + serviceMeta.getName();
        List<String> services = ZK_CLIENT.getChildren(servicePath);
        if(!CollectionUtils.isEmpty(services)) {
            for(String service : services) {
                String address = ZK_CLIENT.readData(servicePath + "/" + service);
                serviceMetas.add(new ServiceMeta(serviceMeta.getName(), address));
            }
        }
        return serviceMetas;
    }

    /**
     * 查找服务并监听服务地址的变化
     *
     * @param serviceMeta
     * @param listener
     * @return
     */
    public static List<ServiceMeta> discover(ServiceMeta serviceMeta, final Listener listener) {
        Assert.notNull(serviceMeta);
        Assert.notNull(serviceMeta.getName());
        List<ServiceMeta> serviceMetas = new ArrayList<ServiceMeta>();
        String servicePath = SERVICE_ROOT_PATH + "/" + serviceMeta.getName();
        List<String> services = ZK_CLIENT.getChildren(servicePath);
        ZK_CLIENT.subscribeChildChanges(servicePath, new IZkChildListener() {
            public void handleChildChange(String parentPath, List<String> list) throws Exception {
                listener.onChange(parentPath, list);
            }
        });
        if(!CollectionUtils.isEmpty(services)) {
            for(String service : services) {
                String address = ZK_CLIENT.readData(servicePath + "/" + service);
                serviceMetas.add(new ServiceMeta(serviceMeta.getName(), address));
            }
        }
        return serviceMetas;
    }

    /**
     * 查找服务并按照一定的策略选择其中一个服务
     *
     * @param serviceMeta serviceMeta
     * @param chooser     选择器
     * @return
     */
    public static ServiceMeta discover(ServiceMeta serviceMeta, Chooser chooser) {
        List<ServiceMeta> serviceMetas = discover(serviceMeta);
        return chooser.choose(serviceMetas);
    }

    /**
     * 查找服务并监听并选择一个服务
     *
     * @param serviceMeta
     * @param chooser
     * @param listener
     * @return
     */
    public static ServiceMeta discover(ServiceMeta serviceMeta, Chooser chooser, final Listener listener) {
        List<ServiceMeta> serviceMetas = discover(serviceMeta, listener);
        return chooser.choose(serviceMetas);
    }

    
    public static List<ServiceMeta> dicoverAll(final Listener listener) {
        List<ServiceMeta> serviceMetas = new ArrayList<ServiceMeta>();
        List<String> services = ZK_CLIENT.getChildren(SERVICE_ROOT_PATH);
        if(CollectionUtils.isEmpty(services)) {
            return serviceMetas;
        }
        ZK_CLIENT.subscribeChildChanges(SERVICE_ROOT_PATH, new IZkChildListener() {
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                listener.onChange(parentPath, currentChilds);
            }
        });
        for(String service : services) {
            List<String> addresses = ZK_CLIENT.getChildren(SERVICE_ROOT_PATH + "/" + service);
            if(!CollectionUtils.isEmpty(addresses)) {
                for(String address : addresses) {
                    serviceMetas.add(new ServiceMeta(service, address));
                }
            }
        }
        return serviceMetas;
    }

}
