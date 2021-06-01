package common.framework.pojo.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * <p>Description: 实体对象父类 </p>
 * @author linan
 * @date 2020-07-23 16:28
 */
@ApiModel(value = "分页返回基础对象")
@Data
@ToString
@NoArgsConstructor
public class BasePage<T> {
    @ApiModelProperty(value = "数据总数")
    private Integer total;
    @ApiModelProperty("数据")
    private T data;

    public BasePage(Integer total, T data) {
        this.total = total;
        this.data = data;
    }
}
