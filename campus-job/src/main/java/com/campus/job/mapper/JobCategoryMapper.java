package com.campus.job.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.common.entity.JobCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 岗位分类Mapper
 */
@Mapper
public interface JobCategoryMapper extends BaseMapper<JobCategory> {
}
