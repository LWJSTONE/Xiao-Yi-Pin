package com.campus.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 岗位发布DTO
 */
@Data
public class JobPostDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 分类ID */
    @NotNull(message = "岗位分类不能为空")
    private Long categoryId;

    /** 岗位标题 */
    @NotBlank(message = "岗位标题不能为空")
    @Size(max = 100, message = "岗位标题不能超过100个字符")
    private String title;

    /** 岗位描述 */
    @NotBlank(message = "岗位描述不能为空")
    @Size(max = 5000, message = "岗位描述不能超过5000个字符")
    private String description;

    /** 工作地点 */
    @NotBlank(message = "工作地点不能为空")
    @Size(max = 200, message = "工作地点不能超过200个字符")
    private String location;

    /** 薪资类型 */
    @NotBlank(message = "薪资类型不能为空")
    private String salaryType;

    /** 薪资金额 */
    @NotNull(message = "薪资金额不能为空")
    @DecimalMin(value = "0.01", message = "薪资金额必须大于0")
    private BigDecimal salaryAmount;

    /** 开始时间 */
    @NotNull(message = "开始时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    /** 结束时间 */
    @NotNull(message = "结束时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    /** 招聘人数 */
    @NotNull(message = "招聘人数不能为空")
    private Integer recruitNum;
}
