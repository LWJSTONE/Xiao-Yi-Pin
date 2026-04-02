package com.campus.auth.controller;

import com.campus.auth.service.AuthService;
import com.campus.common.dto.LoginDTO;
import com.campus.common.dto.RefreshTokenDTO;
import com.campus.common.dto.RegisterDTO;
import com.campus.common.entity.SysUser;
import com.campus.common.result.R;
import com.campus.common.vo.LoginVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Resource
    private AuthService authService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public R<LoginVO> login(@Validated @RequestBody LoginDTO dto) {
        LoginVO loginVO = authService.login(dto);
        return R.ok(loginVO);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public R<Void> register(@Validated @RequestBody RegisterDTO dto) {
        authService.register(dto);
        return R.ok();
    }

    /**
     * 刷新Token
     */
    @PostMapping("/refresh")
    public R<LoginVO> refresh(@Validated @RequestBody RefreshTokenDTO dto) {
        LoginVO loginVO = authService.refreshToken(dto);
        return R.ok(loginVO);
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public R<Void> logout(@RequestHeader("X-User-Id") Long userId) {
        authService.logout(userId);
        return R.ok();
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/current-user")
    public R<SysUser> getCurrentUser(@RequestHeader("X-User-Id") Long userId) {
        SysUser user = authService.getCurrentUser(userId);
        return R.ok(user);
    }
}
