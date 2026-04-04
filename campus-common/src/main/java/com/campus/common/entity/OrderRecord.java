package com.campus.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.campus.common.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("order_record")
public class OrderRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 订单ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 申请ID */
    private Long applicationId;

    /** 学生ID */
    private Long studentId;

    /** 雇主ID */
    private Long employerId;

    /** 岗位ID */
    private Long jobId;

    /** 订单金额 */
    private BigDecimal amount;

    /** 支付状态: 0-待支付, 1-已支付, 2-已退款 */
    private Integer payStatus;

    /** 结算状态: 0-待结算, 1-已结算 */
    private Integer settlementStatus;

    /** 工作开始日期 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startDate;

    /** 工作结束日期 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endDate;
}
