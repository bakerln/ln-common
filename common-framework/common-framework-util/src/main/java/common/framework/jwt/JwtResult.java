package common.framework.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 解析JWT的返回信息
 *
 * @author 宋长军
 * @date 2019/9/24 8:29
 */
@Data
@AllArgsConstructor
public class JwtResult {

    /**
     * JWT 是否有效
     */
    private boolean isValid;

    /**
     * 存储在JWT中的token信息
     */
    private String token;

    /**
     * 该JWT的使用者信息
     */
    private String owner;

    JwtResult(boolean isValid) {
        this.isValid = isValid;
    }
}
