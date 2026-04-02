package com.campus.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.common.entity.JobPost;
import org.apache.ibatis.annotations.Mapper;

/**
 * 岗位Mapper(订单模块使用，用于查询岗位信息及乐观锁更新)
 */
@Mapper
public interface JobPostMapper extends BaseMapper<JobPost> {
}
