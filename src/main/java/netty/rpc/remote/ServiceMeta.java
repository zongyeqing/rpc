package netty.rpc.remote;

/**
 * 请填写类注释
 *
 * @author 宗业清 
 * @since 2018年03月29日
 */
public class ServiceMeta {
    
    /** 服务名 */
    private String name;
    /** 地址 */
    private String address;

    public ServiceMeta() {
    }

    public ServiceMeta(String name) {
        this.name = name;
    }

    public ServiceMeta(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
