package common.framework.global;

/**
 * <p>Description: 全局状态码-枚举类型</p>
 *
 * @author linan
 * @date  2020-10-21
 */
public enum  ReturnCode {

    /**
     * 拦截参数校验
     */
    ILLEGAL_PARAMS_JSON("00421", "JSON格式错误"),
    ILLEGAL_PARAMS_NULL("00422", "请求数据为空"),
    ILLEGAL_PARAMS_CUSTOM("00423", "请求客户信息异常"),
    ILLEGAL_PARAMS_SUFFIX("00424","上传文件仅支持格式为:'doc','docx','xls','xlsx','ppt','pptx','txt','zip','rar','pdf','txt','jpg','jepg','png'的文件"),
    ILLEGAL_PARAMS_LENGTH("00425","文件大小不超过5M"),
    ILLEGAL_PARAMS_BASE64("00426","无效的BASE64格式数据"),

    /**
     * http
     */
    HTTP_SUCCESS("00200", "请求成功！"),
    HTTP_FAILED_SERVER_ERROR("00500", "服务器异常，请稍后重试！"),
    HTTP_FAILED_BAD_REQUEST("00501", "请求错误!"),
    HTTP_FAILED_NO_ACCESS("00503", "禁止访问!"),
    HTTP_FAILED_URI_NOT_FOUND("00504", "请求路径不存在！"),
    HTTP_FAILED_REQUEST_METHOD_ERROR("00505", "非法请求方式！"),
    HTTP_FAILED_GATEWAY_TIMEOUT("00506", "网关超时!"),
    HTTP_FAILED_PARAMS_ERROR("00520", "参数错误"),
    HTTP_FAILED_TOO_MANG_REQUESTS("00529", "限制访问！"),

    /**
     * 业务状态
     */
    BIZ_OPERATION_ERROR("00900", "操作失败！"),
    BIZ_USER_LOGIN_FAIL("00901", "用户名或密码错误！"),

    /**
     * 未归类状态
     */
    STATE_INVALID_TOKEN("00601", "无效的TOKEN!"),
    STATE_NO_PERMISSION_TOKEN("00603", "TOKEN无对应权限!"),
    STATE_INVALID_APPID("00604", "APPID无效!"),
    STATE_NO_PREMISSION_BANAPPID("00605", "APPID已被禁用!"),
    STATE_NO_PREMISSION_OVERDUE("00606", "APPID已过期!"),
    STATE_NO_PERMISSION_URL("00607", "APPID无请求URL权限！"),
    STATE_INVALID_REFRESHTOKEN("00608", "无效的REFRESH_TOKEN!"),
    STATE_INVALID_WX_SHARE("00609", "分享信息已失效!"),
    STATE_AUTH_ERROR("00610", "鉴权服务异常！"),
    STATE_AUTHORIZATION_ERROR("00611", "用户未授权！"),
    STATE_CODE_NOT_EXIST("25001", "模块代码已存在"),
    STATE_EXIST("00430", "已存在"),
    STATE_NOT_EXIST("00431", "数据不存在"),
    STATE_APP_NOT_EXIST("00432", "小程序不存在"),
    STATE_FILE_NOT_EXIST("00633", "文件不存在");

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
    private ReturnCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
