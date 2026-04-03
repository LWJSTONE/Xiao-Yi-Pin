package com.campus.job.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.entity.JobPost;
import com.campus.common.vo.JobPostVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 岗位发布Mapper
 */
@Mapper
public interface JobPostMapper extends BaseMapper<JobPost> {

    /**
     * 查询岗位详情（含发布者名称和分类名称）
     */
    JobPostVO selectJobDetail(@Param("jobId") Long jobId);

    /**
     * 分页查询已发布岗位（含发布者名称和分类名称）
     */
    IPage<JobPostVO> selectJobPage(Page<JobPostVO> page,
                                    @Param("keyword") String keyword,
                                    @Param("categoryId") Long categoryId,
                                    @Param("location") String location,
                                    @Param("salaryType") String salaryType,
                                    @Param("auditStatus") Integer auditStatus,
                                    @Param("status") Integer status);

    /**
     * 分页查询我的岗位
     */
    IPage<JobPostVO> selectMyJobPage(Page<JobPostVO> page,
                                      @Param("publisherId") Long publisherId,
                                      @Param("status") Integer status);
}
