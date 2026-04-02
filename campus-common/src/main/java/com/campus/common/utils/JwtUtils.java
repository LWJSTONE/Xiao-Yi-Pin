package com.campus.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT工具类
 */
public class JwtUtils {

    private JwtUtils() {
    }

    /**
     * 生成Token
     *
     * @param userId      用户ID
     * @param username    用户名
     * @param roleType    角色类型
     * @param expireMillis 过期时间（毫秒）
     * @param secret      密钥
     * @return JWT Token字符串
     */
    public static String generateToken(Long userId, String username, String roleType,
                                       long expireMillis, String secret) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expireMillis);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .claim("roleType", roleType)
                .issuedAt(now)
                .expiration(expireDate)
                .signWith(key)
                .compact();
    }

    /**
     * 解析Token
     *
     * @param token  JWT Token字符串
     * @param secret 密钥
     * @return Claims对象
     */
    public static Claims parseToken(String token, String secret) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            throw new IllegalArgumentException("无效的Token", e);
        }
    }

    /**
     * 判断Token是否过期
     *
     * @param token  JWT Token字符串
     * @param secret 密钥
     * @return true-已过期, false-未过期
     */
    public static boolean isTokenExpired(String token, String secret) {
        try {
            Claims claims = parseToken(token, secret);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 从Token中获取用户ID
     *
     * @param token  JWT Token字符串
     * @param secret 密钥
     * @return 用户ID
     */
    public static Long getUserIdFromToken(String token, String secret) {
        Claims claims = parseToken(token, secret);
        return Long.parseLong(claims.getSubject());
    }
}
