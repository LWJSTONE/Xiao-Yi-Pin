package com.campus.order.service;

import com.campus.common.dto.ApplyDTO;
import com.campus.common.result.PageResult;
import com.campus.common.vo.ApplicationVO;

/**
 * 报名申请服务接口
 */
public interface ApplicationService {

    /**
     * 申请岗位
     */
    void applyJob(Long studentId, Long jobId, ApplyDTO dto);

    /**
     * 审核申请（雇主）
     */
    void reviewApplication(Long employerId, Long applicationId, Integer status, String rejectReason);

    /**
     * 查询我的申请
     */
    PageResult<ApplicationVO> myApplications(Long studentId, int page, int size);

    /**
     * 查询岗位的申请列表
     */
    PageResult<ApplicationVO> jobApplications(Long employerId, Long jobId, int page, int size);

    /**
     * 查询所有申请（管理员）
     */
    PageResult<ApplicationVO> allApplications(int page, int size);
}
