package com.campus.common.utils;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JwtUtils 单元测试
 */
@DisplayName("JWT工具类测试")
class JwtUtilsTest {

    private static final String SECRET = "testsecretkeytestsecretkeytestsecretkey01";
    private static final Long USER_ID = 100L;
    private static final String USERNAME = "testuser";
    private static final String ROLE_TYPE = "STUDENT";
    private static final long EXPIRE_MILLIS = 3600000L; // 1小时

    private String validToken;

    @BeforeEach
    void setUp() {
        validToken = JwtUtils.generateToken(USER_ID, USERNAME, ROLE_TYPE, EXPIRE_MILLIS, SECRET);
    }

    // ==================== generateToken 测试 ====================

    @Test
    @DisplayName("生成Token成功 - 返回非空Token字符串")
    void generateToken_success() {
        // when
        String token = JwtUtils.generateToken(USER_ID, USERNAME, ROLE_TYPE, EXPIRE_MILLIS, SECRET);

        // then
        assertNotNull(token);
        assertTrue(token.length() > 0);
        // JWT由三部分组成，用.分隔
        assertEquals(2, token.chars().filter(ch -> ch == '.').count());
    }

    @Test
    @DisplayName("生成不同参数的Token - 内容各不相同")
    void generateToken_differentParams() {
        // when
        String token1 = JwtUtils.generateToken(1L, "user1", "STUDENT", EXPIRE_MILLIS, SECRET);
        String token2 = JwtUtils.generateToken(2L, "user2", "EMPLOYER", EXPIRE_MILLIS, SECRET);

        // then
        assertNotEquals(token1, token2);
    }

    // ==================== parseToken 测试 ====================

    @Test
    @DisplayName("解析Token成功 - 正确提取所有声明")
    void parseToken_success() {
        // when
        Claims claims = JwtUtils.parseToken(validToken, SECRET);

        // then
        assertNotNull(claims);
        assertEquals(String.valueOf(USER_ID), claims.getSubject());
        assertEquals(USERNAME, claims.get("username"));
        assertEquals(ROLE_TYPE, claims.get("roleType"));
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }

    @Test
    @DisplayName("解析Token失败 - 无效Token抛出IllegalArgumentException")
    void parseToken_invalidToken() {
        // when & then
        assertThrows(IllegalArgumentException.class, () -> JwtUtils.parseToken("invalid.token.here", SECRET));
    }

    @Test
    @DisplayName("解析Token失败 - 错误密钥抛出IllegalArgumentException")
    void parseToken_wrongSecret() {
        // given - 使用不同的密钥生成的token
        String wrongSecretToken = JwtUtils.generateToken(USER_ID, USERNAME, ROLE_TYPE, EXPIRE_MILLIS, "wrongsecretkeywrongsecretkeywrongsecretke");
        // 用正确的密钥去解析用错误密钥生成的token
        assertThrows(IllegalArgumentException.class, () -> JwtUtils.parseToken(wrongSecretToken, SECRET));
    }

    @Test
    @DisplayName("解析Token失败 - null Token抛出IllegalArgumentException")
    void parseToken_nullToken() {
        assertThrows(IllegalArgumentException.class, () -> JwtUtils.parseToken(null, SECRET));
    }

    @Test
    @DisplayName("解析Token失败 - 空Token抛出IllegalArgumentException")
    void parseToken_emptyToken() {
        assertThrows(IllegalArgumentException.class, () -> JwtUtils.parseToken("", SECRET));
    }

    // ==================== isTokenExpired 测试 ====================

    @Test
    @DisplayName("Token未过期 - 返回false")
    void isTokenExpired_false() {
        // when
        boolean expired = JwtUtils.isTokenExpired(validToken, SECRET);

        // then
        assertFalse(expired);
    }

    @Test
    @DisplayName("Token已过期 - 返回true")
    void isTokenExpired_true() {
        // given - 生成一个已经过期的Token (过期时间设为-1ms)
        String expiredToken = JwtUtils.generateToken(USER_ID, USERNAME, ROLE_TYPE, -1000L, SECRET);

        // when
        boolean expired = JwtUtils.isTokenExpired(expiredToken, SECRET);

        // then
        assertTrue(expired);
    }

    @Test
    @DisplayName("判断过期 - 无效Token返回true")
    void isTokenExpired_invalidToken() {
        // when
        boolean expired = JwtUtils.isTokenExpired("invalid.token", SECRET);

        // then
        assertTrue(expired);
    }

    // ==================== getUserIdFromToken 测试 ====================

    @Test
    @DisplayName("从Token获取用户ID成功")
    void getUserIdFromToken_success() {
        // when
        Long userId = JwtUtils.getUserIdFromToken(validToken, SECRET);

        // then
        assertEquals(USER_ID, userId);
    }

    @Test
    @DisplayName("从Token获取用户ID失败 - 无效Token抛出异常")
    void getUserIdFromToken_invalidToken() {
        assertThrows(IllegalArgumentException.class, () -> JwtUtils.getUserIdFromToken("invalid", SECRET));
    }

    // ==================== 边界测试 ====================

    @Test
    @DisplayName("生成Token - 最小过期时间(1ms)仍然有效")
    void generateToken_minExpire() {
        // given
        String token = JwtUtils.generateToken(USER_ID, USERNAME, ROLE_TYPE, 1000L, SECRET);

        // then
        Claims claims = JwtUtils.parseToken(token, SECRET);
        assertNotNull(claims);
        assertEquals(String.valueOf(USER_ID), claims.getSubject());
    }
}
