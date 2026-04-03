package com.campus.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.auth.mapper.SysUserMapper;
import com.campus.auth.service.AuthService;
import com.campus.common.constant.RedisConstant;
import com.campus.common.dto.LoginDTO;
import com.campus.common.dto.RefreshTokenDTO;
import com.campus.common.dto.RegisterDTO;
import com.campus.common.entity.SysUser;
import com.campus.common.exception.BusinessException;
import com.campus.common.utils.JwtUtils;
import com.campus.common.vo.LoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access-token-expire}")
    private long accessTokenExpire;

    @Value("${jwt.refresh-token-expire}")
    private long refreshTokenExpire;

    /** 验证码字符集 */
    private static final String CAPTCHA_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    /** 验证码过期时间(秒) */
    private static final long CAPTCHA_EXPIRE_SECONDS = 300;

    /** 安全随机数生成器 */
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Override
    public Map<String, String> generateCaptcha() {
        // 使用Hutool生成算术验证码
        cn.hutool.captcha.Captcha captcha = cn.hutool.captcha.CaptchaUtil.createShearCaptcha(120, 40, 4, 2);
        String captchaCode = captcha.getCode();

        // 生成唯一key
        String captchaKey = UUID.randomUUID().toString().replace("-", "");

        // 存储到Redis，5分钟过期
        String redisKey = RedisConstant.CAPTCHA_KEY + captchaKey;
        stringRedisTemplate.opsForValue().set(redisKey, captchaCode,
                CAPTCHA_EXPIRE_SECONDS, TimeUnit.SECONDS);

        Map<String, String> result = new HashMap<>();
        result.put("captchaKey", captchaKey);
        // 返回Base64编码的验证码图片（包含data:image前缀）
        result.put("captchaImg", captcha.getImageBase64Data());
        return result;
    }

    @Override
    public LoginVO login(LoginDTO dto) {
        // 1. 验证验证码
        if (dto.getCaptchaKey() == null || dto.getCaptchaCode() == null) {
            throw new BusinessException(1006, "验证码不能为空");
        }
        String captchaRedisKey = RedisConstant.CAPTCHA_KEY + dto.getCaptchaKey();
        String storedCaptcha = stringRedisTemplate.opsForValue().get(captchaRedisKey);
        // 验证后立即删除，防止重复使用
        stringRedisTemplate.delete(captchaRedisKey);
        if (storedCaptcha == null) {
            throw new BusinessException(1006, "验证码已过期，请刷新");
        }
        if (!storedCaptcha.equalsIgnoreCase(dto.getCaptchaCode())) {
            throw new BusinessException(1006, "验证码错误");
        }

        // 2. 根据用户名查询用户
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUsername, dto.getUsername());
        SysUser sysUser = sysUserMapper.selectOne(queryWrapper);
        if (sysUser == null) {
            throw new BusinessException(1001, "用户不存在");
        }

        // 3. 检查用户状态
        if (sysUser.getStatus() != null && sysUser.getStatus() == 0) {
            throw new BusinessException(403, "用户已被禁用");
        }

        // 4. 验证密码
        if (!passwordEncoder.matches(dto.getPassword(), sysUser.getPasswordHash())) {
            throw new BusinessException(1002, "密码错误");
        }

        // 5. 生成Token
        String accessToken = JwtUtils.generateToken(
                sysUser.getId(), sysUser.getUsername(), sysUser.getRoleType(),
                accessTokenExpire, jwtSecret);
        String refreshToken = JwtUtils.generateToken(
                sysUser.getId(), sysUser.getUsername(), sysUser.getRoleType(),
                refreshTokenExpire, jwtSecret);

        // 6. 存储Token到Redis（以登录Token为Key，TTL为访问Token的过期时间）
        String tokenKey = RedisConstant.LOGIN_TOKEN_KEY + sysUser.getId();
        stringRedisTemplate.opsForValue().set(tokenKey, accessToken,
                accessTokenExpire, TimeUnit.MILLISECONDS);

        // 7. 构建返回
        LoginVO loginVO = new LoginVO();
        loginVO.setAccessToken(accessToken);
        loginVO.setRefreshToken(refreshToken);
        loginVO.setExpiresIn(accessTokenExpire / 1000);
        loginVO.setRoleType(sysUser.getRoleType());
        loginVO.setUserId(sysUser.getId());
        loginVO.setUsername(sysUser.getUsername());

        log.info("用户登录成功, userId={}, username={}", sysUser.getId(), sysUser.getUsername());
        return loginVO;
    }

    @Override
    public void register(RegisterDTO dto) {
        // 1. 检查用户名是否已存在
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUsername, dto.getUsername());
        SysUser existUser = sysUserMapper.selectOne(queryWrapper);
        if (existUser != null) {
            throw new BusinessException(1005, "用户名已存在");
        }

        // 2. 创建用户
        SysUser sysUser = new SysUser();
        sysUser.setUsername(dto.getUsername());
        sysUser.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        sysUser.setPhone(dto.getPhone());
        sysUser.setRoleType(dto.getRoleType());
        sysUser.setStatus(1);
        sysUser.setDeleted(0);
        sysUserMapper.insert(sysUser);

        log.info("用户注册成功, username={}, roleType={}", dto.getUsername(), dto.getRoleType());
    }

    @Override
    public LoginVO refreshToken(RefreshTokenDTO dto) {
        // 1. 解析刷新Token
        Long userId;
        String username;
        String roleType;
        try {
            io.jsonwebtoken.Claims claims = JwtUtils.parseToken(dto.getRefreshToken(), jwtSecret);
            userId = Long.parseLong(claims.getSubject());
            username = (String) claims.get("username");
            roleType = (String) claims.get("roleType");
        } catch (Exception e) {
            throw new BusinessException(1003, "刷新Token无效或已过期");
        }

        // 2. 检查黑名单
        String blacklistKey = RedisConstant.USER_BLACKLIST + userId;
        Boolean isBlacklisted = stringRedisTemplate.hasKey(blacklistKey);
        if (Boolean.TRUE.equals(isBlacklisted)) {
            throw new BusinessException(1004, "用户已登出，请重新登录");
        }

        // 3. 生成新的访问Token
        String newAccessToken = JwtUtils.generateToken(userId, username, roleType,
                accessTokenExpire, jwtSecret);
        String newRefreshToken = JwtUtils.generateToken(userId, username, roleType,
                refreshTokenExpire, jwtSecret);

        // 4. 更新Redis中的Token
        String tokenKey = RedisConstant.LOGIN_TOKEN_KEY + userId;
        stringRedisTemplate.opsForValue().set(tokenKey, newAccessToken,
                accessTokenExpire, TimeUnit.MILLISECONDS);

        // 5. 构建返回
        LoginVO loginVO = new LoginVO();
        loginVO.setAccessToken(newAccessToken);
        loginVO.setRefreshToken(newRefreshToken);
        loginVO.setExpiresIn(accessTokenExpire / 1000);
        loginVO.setRoleType(roleType);
        loginVO.setUserId(userId);
        loginVO.setUsername(username);

        log.info("Token刷新成功, userId={}", userId);
        return loginVO;
    }

    @Override
    public void logout(Long userId) {
        // 1. 将用户ID加入黑名单（TTL与刷新Token过期时间一致）
        String blacklistKey = RedisConstant.USER_BLACKLIST + userId;
        stringRedisTemplate.opsForValue().set(blacklistKey, String.valueOf(System.currentTimeMillis()),
                refreshTokenExpire, TimeUnit.MILLISECONDS);

        // 2. 移除登录Token
        String tokenKey = RedisConstant.LOGIN_TOKEN_KEY + userId;
        stringRedisTemplate.delete(tokenKey);

        log.info("用户登出成功, userId={}", userId);
    }

    @Override
    public SysUser getCurrentUser(Long userId) {
        SysUser sysUser = sysUserMapper.selectById(userId);
        if (sysUser == null) {
            throw new BusinessException(1001, "用户不存在");
        }
        return sysUser;
    }
}
