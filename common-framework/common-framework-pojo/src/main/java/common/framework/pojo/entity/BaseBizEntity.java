package common.framework.pojo.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>Description: 业务对象父类 </p>
 * @author linan
 * @date 2020-07-23 16:30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseBizEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1107359832191641066L;
    /**
     * 创建人姓名
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 修改人姓名
     */
    private String modifiedBy;

    /**
     * 修改时间
     */
    private Date modifiedAt;

    /**
     * 删除标记
     */
    private String deleted;
}
