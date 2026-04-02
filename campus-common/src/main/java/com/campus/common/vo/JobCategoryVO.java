package com.campus.common.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 岗位分类VO
 */
@Data
public class JobCategoryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 分类ID */
    private Long id;

    /** 分类名称 */
    private String name;

    /** 父分类ID */
    private Long parentId;

    /** 子分类列表 */
    private List<JobCategoryVO> children;
}
