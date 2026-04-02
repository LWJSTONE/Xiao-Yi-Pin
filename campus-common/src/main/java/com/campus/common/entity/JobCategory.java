package com.campus.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 岗位分类实体
 */
@Data
@TableName("job_category")
public class JobCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 分类ID */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 分类名称 */
    private String name;

    /** 父分类ID */
    private Long parentId;

    /** 排序号 */
    private Integer sortOrder;

    /** 状态: 0-禁用, 1-启用 */
    private Integer status;
}
