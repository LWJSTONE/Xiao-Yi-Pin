package com.campus.common.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录响应VO
 */
@Data
public class LoginVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 访问Token */
    private String accessToken;

    /** 刷新Token */
    private String refreshToken;

    /** 过期时间（秒） */
    private Long expiresIn;

    /** 角色类型 */
    private String roleType;

    /** 用户ID */
    private Long userId;

    /** 用户名 */
    private String username;
}
