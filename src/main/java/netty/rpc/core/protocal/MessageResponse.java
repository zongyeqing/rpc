package netty.rpc.core.protocal;

import java.io.Serializable;

/**
 * 请填写类注释
 *
 * @author 宗业清
 * @since 2017年09月27日
 */
public class MessageResponse implements Serializable {

    private String messageId;
    private String error;
    private Object resultDesc;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Object getResult() {
        return resultDesc;
    }

    public void setResult(Object resultDesc) {
        this.resultDesc = resultDesc;
    }
    
    @Override
    public String toString() {
        return "MessageResponse{" + "messageId='" + messageId + '\'' + ", error='" + error + '\'' + ", resultDesc=" + resultDesc + '}';
    }
}
