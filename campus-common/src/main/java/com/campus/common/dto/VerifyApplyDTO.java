package com.campus.common.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 实名认证申请DTO
 */
@Data
public class VerifyApplyDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 真实姓名 */
    @NotBlank(message = "真实姓名不能为空")
    @Size(max = 50, message = "真实姓名不能超过50个字符")
    private String realName;

    /** 身份证号 */
    @NotBlank(message = "身份证号不能为空")
    @Size(max = 18, message = "身份证号格式不正确")
    private String idCard;

    /** 身份证图片URL */
    @NotBlank(message = "身份证图片不能为空")
    private String idCardImage;

    /** 认证类型 */
    @NotBlank(message = "认证类型不能为空")
    private String verifyType;
}
