package common.framework.jwt;

import common.framework.util.Base64Util;
import io.jsonwebtoken.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.UUID;

/**
 * JWT 工具类
 *
 * @author linan
 * @date  2020-10-21
 */
public class JwtUtil {

    /**
     * 秘钥
     */
    private static final String JWT_SECRET = "WEB-TOKEN";

    /**
     * 创建 JWT 字符串
     *
     * @return jwt
     */
    public static String generateJWT(long expired, String username) {

        // 创建payload私有声明
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        // 生成JWT时间
        long nowMills = System.currentTimeMillis();
        Date now = new Date(nowMills);
        // 过期时间
        Date exp = new Date(nowMills + expired);

        // 生成签名的时候使用secret，这个秘钥不能外漏。属于服务器私钥，任何场景都不能流露，客户端知道了，便可以自我签发JWT
        SecretKey key = generalKey();

        JwtBuilder builder = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                .setIssuer("JWHY-HYJG")
                .setAudience(username)
                .setExpiration(exp)
                .setSubject("Authorization")
                .setIssuedAt(now)
                .setId(UUID.randomUUID().toString())
                .signWith(signatureAlgorithm, key);
        return builder.compact();
    }

    /**
     * 解密 验证jwt是否有效以及是否被篡改
     *
     * @param jwt jwt字符串
     * @return map：{isValid、id、owner}
     */
    public static JwtResult parseJWT(String jwt) {

        // 获取签名秘钥和生成时候一样
        SecretKey key = generalKey();
        JwtResult jwtResult;

        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(jwt)
                    .getBody();
            jwtResult = new JwtResult(true, claims.getId(), claims.getAudience());

        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {

            jwtResult = new JwtResult(false);
        }
        return jwtResult;
    }

    /**
     * 解密 验证jwt是否有效以及是否被篡改
     *
     * @param jwt jwt字符串
     * @return 生成jwt的appid/usernam
     */
    public static String getJWTOwner(String jwt) {

        String owner;
        // 获取签名秘钥和生成时候一样
        SecretKey key = generalKey();
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(jwt)
                    .getBody();
            owner = claims.getAudience();

        } catch (ExpiredJwtException e) {
            owner = "TOKEN已过期";
        } catch (UnsupportedJwtException e) {
            owner = "TOKEN未认证";
        } catch (MalformedJwtException e) {
            owner = "TOKEN已失效";
        } catch (SignatureException e) {
            owner = "TOKEN错误签名";
        } catch (IllegalArgumentException e) {
            owner = "TOKEN非法参数";
        }
        return owner;
    }

    /**
     * 由指定secret字符串生成加密key
     *
     * @return secretKey
     */
    private static SecretKey generalKey() {

        // 本地密码解码
        byte[] encodeKey = Base64Util.decode(JWT_SECRET);
        // 根据指定得字节数组密码使用AES加密算法构造一个秘钥
        return new SecretKeySpec(encodeKey, 0, encodeKey.length, "AES");
    }
}
