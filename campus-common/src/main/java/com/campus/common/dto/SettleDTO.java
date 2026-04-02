package com.campus.common.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 结算DTO
 */
@Data
public class SettleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 结算金额 */
    @NotNull(message = "结算金额不能为空")
    @DecimalMin(value = "0.01", message = "结算金额必须大于0")
    private BigDecimal amount;
}
