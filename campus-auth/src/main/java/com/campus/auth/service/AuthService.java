package com.campus.auth.service;

import com.campus.common.dto.LoginDTO;
import com.campus.common.dto.RefreshTokenDTO;
import com.campus.common.dto.RegisterDTO;
import com.campus.common.entity.SysUser;
import com.campus.common.vo.LoginVO;

import java.util.Map;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 生成验证码
     *
     * @return 包含captchaKey和captchaCode的Map
     */
    Map<String, String> generateCaptcha();

    /**
     * 用户登录
     *
     * @param dto 登录请求
     * @return 登录响应
     */
    LoginVO login(LoginDTO dto);

    /**
     * 用户注册
     */
    void register(RegisterDTO dto);

    /**
     * 刷新Token
     *
     * @param dto 刷新Token请求
     * @return 登录响应
     */
    LoginVO refreshToken(RefreshTokenDTO dto);

    /**
     * 用户登出
     *
     * @param userId 用户ID
     */
    void logout(Long userId);

    /**
     * 获取当前用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    SysUser getCurrentUser(Long userId);
}
