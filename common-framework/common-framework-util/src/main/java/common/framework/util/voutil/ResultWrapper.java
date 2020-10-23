package common.framework.util.voutil;

import common.framework.util.global.ReturnCode;
import common.framework.util.jsonutil.JsonUtil;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>Description: 结果对象包装类 </p>
 * @author linan
 * @date 2020-07-28 15:22
 */
@Data
public class ResultWrapper<T> implements Serializable{

    private static final long serialVersionUID = -2418255843355126652L;

    /**
     * 消息
     */
    private String msg;
    /**
     * 返回码
     */
    private String returnCode;

    /**
     * 数据
     */
    private T data;

    public ResultWrapper() {
        this(ReturnCode.HTTP_SUCCESS);
    }

    public ResultWrapper(ReturnCode returnCode) {
        this.returnCode = returnCode.getReturnCode();
        this.msg = returnCode.getMsg();
    }

    public ResultWrapper(String returnCode, String msg, T data) {
        this.msg = msg;
        this.returnCode = returnCode;
        this.data = data;
    }

    public ResultWrapper(String returnCode, String msg) {
        this.msg = msg;
        this.returnCode = returnCode;
    }

    public ResultWrapper(ReturnCode returnCode, T data) {
        this.returnCode = returnCode.getReturnCode();
        this.msg = returnCode.getMsg();
        this.data = data;
    }

    public ResultWrapper(ReturnCode returnCode, String msg, T data) {
        this.returnCode = returnCode.getReturnCode();
        this.msg = msg;
        this.data = data;
    }


    @Override
    public String toString() {
        return JsonUtil.toJsonStr(this);
    }


}
