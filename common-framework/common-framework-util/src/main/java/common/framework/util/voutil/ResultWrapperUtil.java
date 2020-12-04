package common.framework.util.voutil;

import common.framework.util.global.ReturnCode;

/**
 *
 * <p>Description: 返回对象工具类 </p>
 * @author linan
 * @date 2020-07-28 15:29
 */
public class ResultWrapperUtil {



    /**
     * 自定义访问
     * @param returnCode ReturnCode
     * @param t 数据对象
     * @param <T> 泛型
     * @return ResultWrapper
     */
    public static <T> ResultWrapper custom(ReturnCode returnCode, T t) {
        return new ResultWrapper(returnCode, t);
    }
    public static <T> ResultWrapper custom(ReturnCode returnCode, String msg) {
        return new ResultWrapper(returnCode.getCode(), msg);
    }
    public static <T> ResultWrapper custom(String returnCode, String msg, T t) {
        return new ResultWrapper(returnCode, msg, t);
    }

    /**
     * HTTP 访问成功 200
     */
    public static ResultWrapper success() {
        return new ResultWrapper(ReturnCode.HTTP_SUCCESS);
    }

    public static <T> ResultWrapper success(T t) {
        return new ResultWrapper(ReturnCode.HTTP_SUCCESS, t);
    }

    public static <T> ResultWrapper success(String msg, T t) {
        return new ResultWrapper(ReturnCode.HTTP_SUCCESS, msg, t);
    }


    /**
     * 服务器异常 500
     */
    public static ResultWrapper serverError() {
        return new ResultWrapper(ReturnCode.HTTP_FAILED_SERVER_ERROR);
    }

    public static ResultWrapper serverError(String msg) {
        return new ResultWrapper(ReturnCode.HTTP_FAILED_SERVER_ERROR, msg);
    }

    public static <T> ResultWrapper serverError(T t) {
        return new ResultWrapper(ReturnCode.HTTP_FAILED_SERVER_ERROR, t);
    }
    public static <E> ResultWrapper<E> serverError(Exception exception) {
        return new ResultWrapper<E>(ReturnCode.HTTP_FAILED_SERVER_ERROR, exception.getMessage(), null);
    }

    /**
     * 业务异常 900
     */
    public static ResultWrapper bizError() {
        return new ResultWrapper(ReturnCode.BIZ_OPERATION_ERROR);
    }

    public static ResultWrapper bizError(String msg) {
        return new ResultWrapper(ReturnCode.BIZ_OPERATION_ERROR, msg);
    }

    public static <T> ResultWrapper bizError(String msg,T t) {
        return new ResultWrapper(ReturnCode.BIZ_OPERATION_ERROR, msg, t);
    }

    public static <E> ResultWrapper<E> bizError(Exception exception) {
        return new ResultWrapper<E>(ReturnCode.BIZ_OPERATION_ERROR, exception.getMessage(), null);
    }


    /**
     * 根据Boolean来判断返回是否成功
     */
    public static <T> ResultWrapper successOrError(boolean flag, String msg, T t){
        if(flag){
            return new ResultWrapper(ReturnCode.HTTP_SUCCESS, msg, t);
        }else{
            return new ResultWrapper(ReturnCode.BIZ_OPERATION_ERROR, msg, t);
        }
    }

    public static ResultWrapper successOrError(boolean flag, String msg){
        if(flag){
            return new ResultWrapper(ReturnCode.HTTP_SUCCESS, msg);
        }else{
            return  new ResultWrapper(ReturnCode.BIZ_OPERATION_ERROR, msg);
        }
    }

}
