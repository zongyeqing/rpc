package netty.rpc.zookeeper;

import java.util.List;

/**
 * zookeeper监听器
 *
 * @author 宗业清 
 * @since 2018年03月29日
 */
public interface Listener {

    /**
     * 当数据变化时
     */
    void onChange(String parentPath, List<String> list);
    
}
