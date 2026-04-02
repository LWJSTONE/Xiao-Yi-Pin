package com.campus.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 报名申请VO
 */
@Data
public class ApplicationVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 申请ID */
    private Long id;

    /** 岗位ID */
    private Long jobId;

    /** 岗位标题 */
    private String jobTitle;

    /** 申请人ID */
    private Long applicantId;

    /** 申请人姓名 */
    private String applicantName;

    /** 简历URL */
    private String resumeUrl;

    /** 状态 */
    private Integer status;

    /** 申请时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date applyTime;

    /** 审核时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date reviewTime;

    /** 拒绝原因 */
    private String rejectReason;
}
