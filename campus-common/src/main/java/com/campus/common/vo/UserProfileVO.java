package com.campus.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户资料VO
 */
@Data
public class UserProfileVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户ID */
    private Long userId;

    /** 用户名 */
    private String username;

    /** 真实姓名 */
    private String realName;

    /** 性别 */
    private Integer gender;

    /** 学校 */
    private String university;

    /** 专业 */
    private String major;

    /** 年级 */
    private String grade;

    /** 余额 */
    private BigDecimal balance;

    /** 信用分 */
    private Integer creditScore;

    /** 头像URL */
    private String avatarUrl;

    /** 实名认证状态 */
    private Integer verifiedStatus;

    /** 状态(0禁用/1启用) */
    private Integer status;

    /** 角色类型 */
    private String roleType;

    /** 手机号 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
