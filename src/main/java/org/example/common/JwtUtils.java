package org.example.common;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * JWT 工具类
 * 负责 token 的生成与校验
 */
@Component
public class JwtUtils {

    /** 密钥 */
    @Value("${jwt.secret}")
    private String secret;

    /** 过期时间（毫秒），默认7天 */
    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * 生成 JWT Token
     *
     * @param userId 用户ID
     * @param openid 微信openid（可空）
     * @return token 字符串
     */
    public String generateToken(Long userId, String openid) {
        Date now = new Date();
        Date expireAt = new Date(now.getTime() + expiration);

        return JWT.create()
                .withClaim("userId", userId)
                .withClaim("openid", openid)
                .withIssuedAt(now)
                .withExpiresAt(expireAt)
                .sign(Algorithm.HMAC256(secret));
    }

    /**
     * 校验 Token 是否有效
     *
     * @param token JWT 字符串
     * @return true-有效，false-无效/过期
     */
    public boolean verifyToken(String token) {
        try {
            JWT.require(Algorithm.HMAC256(secret)).build().verify(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    /**
     * 从 Token 中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getClaim("userId").asLong();
    }

    /**
     * 从 Token 中获取 openid
     */
    public String getOpenidFromToken(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getClaim("openid").asString();
    }
}
