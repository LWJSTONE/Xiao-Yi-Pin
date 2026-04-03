package com.campus.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.campus.common.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 用户详情实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_profile")
public class UserProfile extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 真实姓名 */
    private String realName;

    /** 身份证哈希 */
    @JsonIgnore
    private String idCardHash;

    /** 性别: 0-未知, 1-男, 2-女 */
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

    /** 身份证图片URL（用于实名认证审核） */
    private String idCardImage;

    /** 实名认证状态: 0-未认证, 1-审核中, 2-已认证, 3-认证失败 */
    private Integer verifiedStatus;
}
