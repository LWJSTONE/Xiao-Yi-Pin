package com.campus.common.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 刷新Token请求DTO
 */
@Data
public class RefreshTokenDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 刷新Token */
    @NotBlank(message = "刷新Token不能为空")
    private String refreshToken;
}
