package common.framework.util.global;

/**
 * <p>Description: 全局状态码-枚举类型</p>
 *
 * @author linan
 * @date 2020-12-02
 */
public enum  ConfigCode {

    SERVICE_STATUS_DOWN("500000","服务状态宕机"),
    SERVICE_STATUS_OUTOFSERVICE("500001","服务状态不可用"),
    SERVICE_STATUS_UP("500002","服务状态启用");

    /**
     * 状态码
     */
    final String code;
    /**
     * 信息
     */
    final String msg;

    /**
     * <p>Description: 返回状态码 </p>
     * @return returnCode 状态码
     */
    public String getCode() {
        return this.code;
    }

    /**
     * <p>Description: 返回信息 </p>
     * @return msg 信息
     */
    public String getMsg() {
        return this.msg;
    }

    /**
     * 构造函数
     * @param code 状态码
     * @param msg 信息
     */
    private ConfigCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
