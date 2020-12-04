package common.framework.util.voutil;

import common.framework.util.global.ReturnCode;
import common.framework.util.jsonutil.JsonUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>Description: 结果对象包装类 </p>
 * @author linan
 * @date 2020-07-28 15:22
 */
@Data
@ApiModel(value="结果对象包装类",description="Http请求默认返回对象" )
public class ResultWrapper<T> implements Serializable{

    private static final long serialVersionUID = -2418255843355126652L;

    /**
     * 消息
     */
    @ApiModelProperty(value = "消息", required = true, example="访问成功")
    private String msg;
    /**
     * 返回码
     */
    @ApiModelProperty(value = "返回码", required = true, example="00200")
    private String returnCode;
    /**NULL
     * 数据
     */
    @ApiModelProperty(value = "数据对象", required = true, example="Null")
    private T data;

    public ResultWrapper() {
        this(ReturnCode.HTTP_SUCCESS);
    }

    public ResultWrapper(ReturnCode returnCode) {
        this.returnCode = returnCode.getCode();
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
        this.returnCode = returnCode.getCode();
        this.msg = returnCode.getMsg();
        this.data = data;
    }

    public ResultWrapper(ReturnCode returnCode, String msg, T data) {
        this.returnCode = returnCode.getCode();
        this.msg = msg;
        this.data = data;
    }


    @Override
    public String toString() {
        return JsonUtil.toJsonStr(this);
    }


}
