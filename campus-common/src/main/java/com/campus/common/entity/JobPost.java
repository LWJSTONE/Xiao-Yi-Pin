package com.campus.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.campus.common.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 岗位发布实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("job_post")
public class JobPost extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 岗位ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 发布者ID */
    private Long publisherId;

    /** 分类ID */
    private Long categoryId;

    /** 岗位标题 */
    private String title;

    /** 岗位描述 */
    private String description;

    /** 工作地点 */
    private String location;

    /** 薪资类型 */
    private String salaryType;

    /** 薪资金额 */
    private java.math.BigDecimal salaryAmount;

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

    /** 审核备注 */
    private String auditRemark;

    /** 乐观锁版本号 */
    @Version
    private Integer version;

    /** 逻辑删除: 0-未删除, 1-已删除 */
    @TableLogic
    private Integer deleted;
}
