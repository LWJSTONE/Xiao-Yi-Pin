package com.campus.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.campus.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统用户实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 用户ID */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 用户名 */
    private String username;

    /** 密码哈希 */
    private String passwordHash;

    /** 手机号 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 角色类型: STUDENT/EMPLOYER/ADMIN */
    private String roleType;

    /** 状态: 0-禁用, 1-启用 */
    private Integer status;

    /** 逻辑删除: 0-未删除, 1-已删除 */
    @TableLogic
    private Integer deleted;
}
