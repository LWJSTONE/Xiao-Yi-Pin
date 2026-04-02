package com.campus.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 岗位信息VO
 */
@Data
public class JobPostVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 岗位ID */
    private Long id;

    /** 发布者ID */
    private Long publisherId;

    /** 发布者名称 */
    private String publisherName;

    /** 分类ID */
    private Long categoryId;

    /** 分类名称 */
    private String categoryName;

    /** 岗位标题 */
    private String title;

    /** 岗位描述 */
    private String description;

    /** 工作地点 */
    private String location;

    /** 薪资类型 */
    private String salaryType;

    /** 薪资金额 */
    private BigDecimal salaryAmount;

    /** 开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    /** 结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    /** 招聘人数 */
    private Integer recruitNum;

    /** 已录用人数 */
    private Integer hiredNum;

    /** 状态 */
    private Integer status;

    /** 审核状态 */
    private Integer auditStatus;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
