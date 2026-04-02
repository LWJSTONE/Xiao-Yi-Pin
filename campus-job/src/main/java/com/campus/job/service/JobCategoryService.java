package com.campus.job.service;

import com.campus.common.vo.JobCategoryVO;

import java.util.List;

/**
 * 岗位分类服务接口
 */
public interface JobCategoryService {

    /**
     * 获取分类树
     */
    List<JobCategoryVO> getCategoryTree();
}
