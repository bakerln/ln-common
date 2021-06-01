package common.framework.pojo.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>Description: 实体对象父类 </p>
 * @author linan
 * @date 2020-07-23 16:28
 */
@Data
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 786851139986974974L;
    /**
     * 主键
     */
    private Integer id;
}
