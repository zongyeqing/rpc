package netty.rpc.core.protocal;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 请填写类注释
 *
 * @author 宗业清
 * @since 2017年09月27日
 */
public class MessageRequest implements Serializable{

    private String messageId;
    private String className;
    private String methodName;
    private Class<?>[] typeParameters;
    private Object[] parametersVal;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getTypeParameters() {
        return typeParameters;
    }

    public void setTypeParameters(Class<?>[] typeParameters) {
        this.typeParameters = typeParameters;
    }

    public Object[] getParametersVal() {
        return parametersVal;
    }

    public void setParametersVal(Object[] parametersVal) {
        this.parametersVal = parametersVal;
    }

    @Override
    public String toString() {
        return "MessageRequest{" + "messageId='" + messageId + '\'' + ", className='" + className + '\'' + ", methodName='" + methodName + '\'' + ", typeParameters=" + Arrays.toString(typeParameters) + ", parametersVal=" + Arrays.toString(parametersVal) + '}';
    }
}
