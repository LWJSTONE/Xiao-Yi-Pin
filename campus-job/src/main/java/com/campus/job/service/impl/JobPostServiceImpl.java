package com.campus.job.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.dto.AuditDTO;
import com.campus.common.dto.JobPostDTO;
import com.campus.common.dto.JobQueryDTO;
import com.campus.common.entity.JobPost;
import com.campus.common.exception.BusinessException;
import com.campus.common.result.PageResult;
import com.campus.common.vo.JobPostVO;
import com.campus.job.mapper.JobPostMapper;
import com.campus.job.service.JobPostService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 岗位服务实现
 */
@Service
public class JobPostServiceImpl implements JobPostService {

    private final JobPostMapper jobPostMapper;

    public JobPostServiceImpl(JobPostMapper jobPostMapper) {
        this.jobPostMapper = jobPostMapper;
    }

    @Override
    public void publishJob(Long publisherId, JobPostDTO dto) {
        JobPost jobPost = new JobPost();
        BeanUtils.copyProperties(dto, jobPost);
        jobPost.setPublisherId(publisherId);
        jobPost.setStatus(0); // 草稿
        jobPost.setAuditStatus(0);
        jobPost.setHiredNum(0);
        jobPostMapper.insert(jobPost);
    }

    @Override
    public void updateJob(Long publisherId, Long jobId, JobPostDTO dto) {
        JobPost jobPost = jobPostMapper.selectById(jobId);
        if (jobPost == null) {
            throw new BusinessException("岗位不存在");
        }
        if (!jobPost.getPublisherId().equals(publisherId)) {
            throw new BusinessException("无权修改此岗位");
        }
        // 只有草稿(0)或审核拒绝(2)状态可以修改
        if (jobPost.getStatus() != 0 && jobPost.getAuditStatus() != 2) {
            throw new BusinessException("当前状态不允许修改");
        }

        jobPost.setCategoryId(dto.getCategoryId());
        jobPost.setTitle(dto.getTitle());
        jobPost.setDescription(dto.getDescription());
        jobPost.setLocation(dto.getLocation());
        jobPost.setSalaryType(dto.getSalaryType());
        jobPost.setSalaryAmount(dto.getSalaryAmount());
        jobPost.setStartTime(dto.getStartTime());
        jobPost.setEndTime(dto.getEndTime());
        jobPost.setRecruitNum(dto.getRecruitNum());
        jobPostMapper.updateById(jobPost);
    }

    @Override
    public void submitAudit(Long publisherId, Long jobId) {
        JobPost jobPost = jobPostMapper.selectById(jobId);
        if (jobPost == null) {
            throw new BusinessException("岗位不存在");
        }
        if (!jobPost.getPublisherId().equals(publisherId)) {
            throw new BusinessException("无权操作此岗位");
        }
        if (jobPost.getStatus() != 0) {
            throw new BusinessException("只有草稿状态可以提交审核");
        }
        jobPost.setStatus(1); // 审核中
        jobPost.setAuditStatus(0);
        jobPostMapper.updateById(jobPost);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditJob(Long jobId, AuditDTO dto, String operatorRole) {
        if (!"ADMIN".equals(operatorRole)) {
            throw new BusinessException(403, "无权审核");
        }
        JobPost jobPost = jobPostMapper.selectById(jobId);
        if (jobPost == null) {
            throw new BusinessException("岗位不存在");
        }
        if (jobPost.getStatus() != 1) {
            throw new BusinessException("该岗位不在审核中状态");
        }

        jobPost.setAuditStatus(dto.getAuditStatus());
        jobPost.setAuditRemark(dto.getAuditRemark());

        if (dto.getAuditStatus() == 1) {
            // 审核通过，状态改为已发布
            jobPost.setStatus(2);
        } else if (dto.getAuditStatus() == 2) {
            // 审核拒绝，状态改回草稿
            jobPost.setStatus(0);
        }

        jobPostMapper.updateById(jobPost);
    }

    @Override
    public void offlineJob(Long publisherId, Long jobId) {
        JobPost jobPost = jobPostMapper.selectById(jobId);
        if (jobPost == null) {
            throw new BusinessException("岗位不存在");
        }
        if (!jobPost.getPublisherId().equals(publisherId)) {
            throw new BusinessException("无权操作此岗位");
        }
        if (jobPost.getStatus() != 2) {
            throw new BusinessException("只有已发布状态可以下线");
        }
        jobPost.setStatus(4); // 已下线
        jobPostMapper.updateById(jobPost);
    }

    @Override
    public JobPostVO getJobDetail(Long jobId) {
        JobPostVO vo = jobPostMapper.selectJobDetail(jobId);
        if (vo == null) {
            throw new BusinessException("岗位不存在");
        }
        return vo;
    }

    @Override
    public PageResult<JobPostVO> listJobs(JobQueryDTO dto) {
        Page<JobPostVO> page = new Page<>(dto.getPage(), dto.getSize());
        IPage<JobPostVO> result = jobPostMapper.selectJobPage(
                page,
                dto.getKeyword(),
                dto.getCategoryId(),
                dto.getLocation(),
                dto.getSalaryType(),
                dto.getAuditStatus(),
                dto.getStatus()
        );
        return PageResult.of(result);
    }

    @Override
    public PageResult<JobPostVO> getMyJobs(Long publisherId, Integer status, int page, int size) {
        Page<JobPostVO> pageParam = new Page<>(page, size);
        IPage<JobPostVO> result = jobPostMapper.selectMyJobPage(pageParam, publisherId, status);
        return PageResult.of(result);
    }
}
