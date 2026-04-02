package com.campus.common.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 报名申请DTO
 */
@Data
public class ApplyDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 简历URL */
    @NotBlank(message = "简历不能为空")
    private String resumeUrl;
}
