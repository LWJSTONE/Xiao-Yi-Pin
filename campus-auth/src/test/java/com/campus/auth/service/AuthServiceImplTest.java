package com.campus.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.auth.mapper.SysUserMapper;
import com.campus.auth.service.impl.AuthServiceImpl;
import com.campus.common.constant.RedisConstant;
import com.campus.common.dto.LoginDTO;
import com.campus.common.dto.RefreshTokenDTO;
import com.campus.common.entity.SysUser;
import com.campus.common.exception.BusinessException;
import com.campus.common.vo.LoginVO;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AuthServiceImpl 单元测试
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("认证服务测试")
class AuthServiceImplTest {

    @Mock
    private SysUserMapper sysUserMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private AuthServiceImpl authService;

    private static final String JWT_SECRET = "testsecretkeytestsecretkeytestsecretkey01";
    private static final Long ACCESS_TOKEN_EXPIRE = 3600000L;
    private static final Long REFRESH_TOKEN_EXPIRE = 604800000L;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "jwtSecret", JWT_SECRET);
        ReflectionTestUtils.setField(authService, "accessTokenExpire", ACCESS_TOKEN_EXPIRE);
        ReflectionTestUtils.setField(authService, "refreshTokenExpire", REFRESH_TOKEN_EXPIRE);
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    private SysUser buildSysUser() {
        SysUser user = new SysUser();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPasswordHash("encodedPassword123");
        user.setRoleType("STUDENT");
        user.setStatus(1);
        user.setPhone("13812345678");
        user.setEmail("test@example.com");
        return user;
    }

    private LoginDTO buildLoginDTO() {
        LoginDTO dto = new LoginDTO();
        dto.setUsername("testuser");
        dto.setPassword("password123");
        dto.setCaptchaCode("1234");
        dto.setCaptchaKey("captcha_key");
        return dto;
    }

    // ==================== login 测试 ====================

    @Test
    @DisplayName("登录成功 - 正确的用户名和密码")
    void login_success() {
        // given
        LoginDTO dto = buildLoginDTO();
        SysUser user = buildSysUser();

        when(sysUserMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(user);
        when(passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())).thenReturn(true);

        // when
        LoginVO result = authService.login(dto);

        // then
        assertNotNull(result);
        assertNotNull(result.getAccessToken());
        assertNotNull(result.getRefreshToken());
        assertEquals(ACCESS_TOKEN_EXPIRE / 1000, result.getExpiresIn());
        assertEquals("STUDENT", result.getRoleType());
        assertEquals(1L, result.getUserId());

        // 验证Token存储到Redis
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(valueOperations).set(keyCaptor.capture(), anyString(), eq(ACCESS_TOKEN_EXPIRE), eq(TimeUnit.MILLISECONDS));
        assertTrue(keyCaptor.getValue().startsWith(RedisConstant.LOGIN_TOKEN_KEY));
    }

    @Test
    @DisplayName("登录失败 - 用户不存在")
    void login_userNotFound() {
        // given
        LoginDTO dto = buildLoginDTO();
        when(sysUserMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> authService.login(dto));
        assertEquals(1001, exception.getCode());
        assertEquals("用户不存在", exception.getMessage());
    }

    @Test
    @DisplayName("登录失败 - 密码错误")
    void login_wrongPassword() {
        // given
        LoginDTO dto = buildLoginDTO();
        SysUser user = buildSysUser();
        when(sysUserMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(user);
        when(passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())).thenReturn(false);

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> authService.login(dto));
        assertEquals(1002, exception.getCode());
        assertEquals("密码错误", exception.getMessage());
    }

    @Test
    @DisplayName("登录失败 - 用户已被禁用")
    void login_userDisabled() {
        // given
        LoginDTO dto = buildLoginDTO();
        SysUser user = buildSysUser();
        user.setStatus(0);
        when(sysUserMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(user);

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> authService.login(dto));
        assertEquals(403, exception.getCode());
        assertEquals("用户已被禁用", exception.getMessage());
    }

    // ==================== refreshToken 测试 ====================

    @Test
    @DisplayName("刷新Token成功 - 有效的刷新Token")
    void refreshToken_success() {
        // given
        String refreshToken = com.campus.common.utils.JwtUtils.generateToken(
                1L, "testuser", "STUDENT", REFRESH_TOKEN_EXPIRE, JWT_SECRET);
        RefreshTokenDTO dto = new RefreshTokenDTO();
        dto.setRefreshToken(refreshToken);

        when(stringRedisTemplate.hasKey(anyString())).thenReturn(false);

        // when
        LoginVO result = authService.refreshToken(dto);

        // then
        assertNotNull(result);
        assertNotNull(result.getAccessToken());
        assertNotNull(result.getRefreshToken());
        assertEquals(ACCESS_TOKEN_EXPIRE / 1000, result.getExpiresIn());
        assertEquals("STUDENT", result.getRoleType());
        assertEquals(1L, result.getUserId());
    }

    @Test
    @DisplayName("刷新Token失败 - 无效的刷新Token")
    void refreshToken_invalidToken() {
        // given
        RefreshTokenDTO dto = new RefreshTokenDTO();
        dto.setRefreshToken("invalid.token.here");

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> authService.refreshToken(dto));
        assertEquals(1003, exception.getCode());
        assertEquals("刷新Token无效或已过期", exception.getMessage());
    }

    @Test
    @DisplayName("刷新Token失败 - 用户已登出(在黑名单中)")
    void refreshToken_userBlacklisted() {
        // given
        String refreshToken = com.campus.common.utils.JwtUtils.generateToken(
                1L, "testuser", "STUDENT", REFRESH_TOKEN_EXPIRE, JWT_SECRET);
        RefreshTokenDTO dto = new RefreshTokenDTO();
        dto.setRefreshToken(refreshToken);

        when(stringRedisTemplate.hasKey(anyString())).thenReturn(true);

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> authService.refreshToken(dto));
        assertEquals(1004, exception.getCode());
        assertEquals("用户已登出，请重新登录", exception.getMessage());
    }

    // ==================== logout 测试 ====================

    @Test
    @DisplayName("登出成功 - 加入黑名单并移除Token")
    void logout_success() {
        // given
        Long userId = 1L;

        // when
        authService.logout(userId);

        // then
        // 验证用户ID加入黑名单
        ArgumentCaptor<String> blacklistKeyCaptor = ArgumentCaptor.forClass(String.class);
        verify(valueOperations).set(blacklistKeyCaptor.capture(), anyString(), eq(REFRESH_TOKEN_EXPIRE), eq(TimeUnit.MILLISECONDS));
        assertTrue(blacklistKeyCaptor.getValue().startsWith(RedisConstant.USER_BLACKLIST));

        // 验证移除登录Token
        ArgumentCaptor<String> tokenKeyCaptor = ArgumentCaptor.forClass(String.class);
        verify(stringRedisTemplate).delete(tokenKeyCaptor.capture());
        assertTrue(tokenKeyCaptor.getValue().startsWith(RedisConstant.LOGIN_TOKEN_KEY));
    }

    // ==================== getCurrentUser 测试 ====================

    @Test
    @DisplayName("获取当前用户成功 - 用户存在")
    void getCurrentUser_success() {
        // given
        Long userId = 1L;
        SysUser user = buildSysUser();
        when(sysUserMapper.selectById(userId)).thenReturn(user);

        // when
        SysUser result = authService.getCurrentUser(userId);

        // then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
        assertEquals("STUDENT", result.getRoleType());
    }

    @Test
    @DisplayName("获取当前用户失败 - 用户不存在")
    void getCurrentUser_notFound() {
        // given
        Long userId = 999L;
        when(sysUserMapper.selectById(userId)).thenReturn(null);

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> authService.getCurrentUser(userId));
        assertEquals(1001, exception.getCode());
        assertEquals("用户不存在", exception.getMessage());
    }
}
