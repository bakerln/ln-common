package common.framework.pojo.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @description 实体对象父类
 * @author linan
 * @date 2020-07-23 16:28
 */
@Data
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 786851139986974974L;
    /**
     * 主键UUID
     */
    private Integer uuid;
}
