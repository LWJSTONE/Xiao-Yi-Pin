package com.campus.common.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 注册请求DTO
 */
@Data
public class RegisterDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度为3到20个字符")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度为6到20个字符")
    private String password;

    @NotBlank(message = "手机号不能为空")
    private String phone;

    @NotBlank(message = "角色类型不能为空")
    @Pattern(regexp = "STUDENT|EMPLOYER", message = "角色类型只能是STUDENT或EMPLOYER")
    private String roleType;
}
