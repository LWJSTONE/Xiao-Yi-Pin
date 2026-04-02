package com.campus.common.dto;

import lombok.Data;

import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 用户资料更新DTO
 */
@Data
public class UserProfileDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 真实姓名 */
    @Size(max = 50, message = "真实姓名不能超过50个字符")
    private String realName;

    /** 性别: 0-未知, 1-男, 2-女 */
    private Integer gender;

    /** 学校 */
    @Size(max = 100, message = "学校名称不能超过100个字符")
    private String university;

    /** 专业 */
    @Size(max = 100, message = "专业名称不能超过100个字符")
    private String major;

    /** 年级 */
    @Size(max = 20, message = "年级不能超过20个字符")
    private String grade;

    /** 头像URL */
    @Size(max = 500, message = "头像URL不能超过500个字符")
    private String avatarUrl;
}
