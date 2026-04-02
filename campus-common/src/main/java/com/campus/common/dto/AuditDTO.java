package com.campus.common.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 审核DTO
 */
@Data
public class AuditDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 审核状态 */
    @NotNull(message = "审核状态不能为空")
    private Integer auditStatus;

    /** 审核备注 */
    private String auditRemark;
}
