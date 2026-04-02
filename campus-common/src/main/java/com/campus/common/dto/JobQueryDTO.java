package com.campus.common.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 岗位查询DTO
 */
@Data
public class JobQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 搜索关键词 */
    private String keyword;

    /** 分类ID */
    private Long categoryId;

    /** 工作地点 */
    private String location;

    /** 薪资类型 */
    private String salaryType;

    /** 审核状态 */
    private Integer auditStatus;

    /** 当前页码 */
    private Integer page = 1;

    /** 每页大小 */
    private Integer size = 10;
}
