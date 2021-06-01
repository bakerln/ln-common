package common.framework.pojo.entity;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * <p>Description: 实体对象父类 </p>
 * @author linan
 * @date 2020-07-23 16:28
 */
@Data
@ToString
public class BasePageSearchVO {
    @NotNull
    private Integer pageIndex = 1;
    @NotNull
    private Integer pageSize = 20;

    public BasePageSearchVO() {
    }

    public BasePageSearchVO(@NotNull Integer pageIndex, @NotNull Integer pageSize) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }
}
