package com.campus.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单记录VO
 */
@Data
public class OrderRecordVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 订单ID */
    private Long id;

    /** 申请ID */
    private Long applicationId;

    /** 岗位标题 */
    private String jobTitle;

    /** 学生ID */
    private Long studentId;

    /** 学生姓名 */
    private String studentName;

    /** 雇主ID */
    private Long employerId;

    /** 雇主姓名 */
    private String employerName;

    /** 订单金额 */
    private BigDecimal amount;

    /** 支付状态 */
    private Integer payStatus;

    /** 结算状态 */
    private Integer settlementStatus;

    /** 工作开始日期 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startDate;

    /** 工作结束日期 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endDate;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
