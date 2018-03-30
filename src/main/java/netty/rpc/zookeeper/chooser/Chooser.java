package netty.rpc.zookeeper.chooser;

import netty.rpc.remote.ServiceMeta;

import java.util.List;

/**
 * 服务选择器
 *
 * @author 宗业清 
 * @since 2018年03月29日
 */
public interface Chooser {

    /**
     * 从多服务中选择一个服务
     * @param serviceMetas services
     * @return choosed services
     */
    ServiceMeta choose(List<ServiceMeta> serviceMetas);
}
