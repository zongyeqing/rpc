package netty.rpc.zookeeper.chooser;

import netty.rpc.remote.ServiceMeta;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Random;

/**
 * 随机选择器
 *
 * @author 宗业清 
 * @since 2018年03月29日
 */
public class RandomChooser implements Chooser {
    
    @Override
    public ServiceMeta choose(List<ServiceMeta> serviceMetas) {
        if(CollectionUtils.isEmpty(serviceMetas)) {
            return null;
        }
        int random = new Random().nextInt(serviceMetas.size());
        return serviceMetas.get(random);
    }
}
