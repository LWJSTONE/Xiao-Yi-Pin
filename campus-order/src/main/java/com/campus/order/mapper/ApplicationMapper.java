package com.campus.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.entity.Application;
import com.campus.common.vo.ApplicationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 报名申请Mapper
 */
@Mapper
public interface ApplicationMapper extends BaseMapper<Application> {

    /**
     * 分页查询我的申请（含岗位标题）
     */
    IPage<ApplicationVO> selectMyApplicationPage(Page<ApplicationVO> page,
                                                  @Param("applicantId") Long applicantId);

    /**
     * 分页查询岗位的申请（含申请人姓名）
     */
    IPage<ApplicationVO> selectJobApplicationPage(Page<ApplicationVO> page,
                                                   @Param("jobId") Long jobId);

    /**
     * 分页查询所有申请（管理员）
     */
    IPage<ApplicationVO> selectAllApplicationPage(Page<ApplicationVO> page);

    /**
     * 分页查询雇主的申请列表（按雇主拥有的岗位过滤）
     */
    IPage<ApplicationVO> selectEmployerApplicationPage(Page<ApplicationVO> page,
                                                         @Param("employerId") Long employerId);
}
