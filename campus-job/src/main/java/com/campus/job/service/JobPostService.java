package com.campus.job.service;

import com.campus.common.dto.AuditDTO;
import com.campus.common.dto.JobPostDTO;
import com.campus.common.dto.JobQueryDTO;
import com.campus.common.result.PageResult;
import com.campus.common.vo.JobPostVO;

/**
 * 岗位服务接口
 */
public interface JobPostService {

    /**
     * 发布岗位（草稿）
     */
    void publishJob(Long publisherId, JobPostDTO dto);

    /**
     * 更新岗位
     */
    void updateJob(Long publisherId, Long jobId, JobPostDTO dto);

    /**
     * 提交审核
     */
    void submitAudit(Long publisherId, Long jobId);

    /**
     * 审核岗位
     */
    void auditJob(Long jobId, AuditDTO dto, String operatorRole);

    /**
     * 下线岗位
     */
    void offlineJob(Long publisherId, Long jobId);

    /**
     * 获取岗位详情
     */
    JobPostVO getJobDetail(Long jobId);

    /**
     * 分页查询岗位列表
     */
    PageResult<JobPostVO> listJobs(JobQueryDTO dto);

    /**
     * 查询我发布的岗位
     */
    PageResult<JobPostVO> getMyJobs(Long publisherId, int page, int size);
}
