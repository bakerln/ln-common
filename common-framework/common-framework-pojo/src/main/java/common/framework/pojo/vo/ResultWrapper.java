package common.framework.pojo.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @description 结果对象包装类
 * @author linan
 * @date 2020-07-28 15:22
 */
@Data
public class ResultWrapper<T> implements Serializable{

    private static final long serialVersionUID = -2418255843355126652L;

    /**
     * 返回码
     */
    private Integer code;
    /**
     * 消息
     */
    private String message;
    /**
     * 数据对象
     */
    private T data;
}
