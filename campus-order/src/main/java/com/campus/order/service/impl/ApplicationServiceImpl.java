package com.campus.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.constant.RedisConstant;
import com.campus.common.dto.ApplyDTO;
import com.campus.common.entity.Application;
import com.campus.common.entity.JobPost;
import com.campus.common.entity.OrderRecord;
import com.campus.common.exception.BusinessException;
import com.campus.common.result.PageResult;
import com.campus.common.vo.ApplicationVO;
import com.campus.order.mapper.ApplicationMapper;
import com.campus.order.mapper.JobPostMapper;
import com.campus.order.mapper.OrderRecordMapper;
import com.campus.order.service.ApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 报名申请服务实现
 */
@Service
public class ApplicationServiceImpl implements ApplicationService {

    private static final Logger log = LoggerFactory.getLogger(ApplicationServiceImpl.class);

    /** 分布式锁过期时间(秒) */
    private static final long LOCK_EXPIRE_SECONDS = 10;

    private final ApplicationMapper applicationMapper;
    private final OrderRecordMapper orderRecordMapper;
    private final JobPostMapper jobPostMapper;
    private final StringRedisTemplate stringRedisTemplate;

    public ApplicationServiceImpl(ApplicationMapper applicationMapper,
                                   OrderRecordMapper orderRecordMapper,
                                   JobPostMapper jobPostMapper,
                                   StringRedisTemplate stringRedisTemplate) {
        this.applicationMapper = applicationMapper;
        this.orderRecordMapper = orderRecordMapper;
        this.jobPostMapper = jobPostMapper;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public void applyJob(Long studentId, Long jobId, ApplyDTO dto) {
        // 获取分布式锁，防止同一岗位并发超卖
        String lockKey = RedisConstant.JOB_LOCK + jobId;
        Boolean locked = stringRedisTemplate.opsForValue()
                .setIfAbsent(lockKey, "1", LOCK_EXPIRE_SECONDS, TimeUnit.SECONDS);
        if (Boolean.FALSE.equals(locked)) {
            throw new BusinessException("操作太频繁，请稍后再试");
        }

        try {
            // 查询岗位信息，校验岗位状态和招聘人数
            JobPost jobPost = jobPostMapper.selectById(jobId);
            if (jobPost == null) {
                throw new BusinessException("岗位不存在");
            }
            if (jobPost.getStatus() != 2) {
                throw new BusinessException("该岗位当前不可报名");
            }
            if (jobPost.getHiredNum() >= jobPost.getRecruitNum()) {
                throw new BusinessException("该岗位招聘人数已满");
            }

            // 检查是否重复申请
            Long count = applicationMapper.selectCount(
                    new LambdaQueryWrapper<Application>()
                            .eq(Application::getJobId, jobId)
                            .eq(Application::getApplicantId, studentId)
            );
            if (count > 0) {
                throw new BusinessException("您已申请过该岗位，请勿重复申请");
            }

            Application application = new Application();
            application.setJobId(jobId);
            application.setApplicantId(studentId);
            application.setResumeUrl(dto.getResumeUrl());
            application.setStatus(0); // 待审核
            application.setApplyTime(new Date());
            applicationMapper.insert(application);

            log.info("学生{}成功报名岗位{}", studentId, jobId);
        } finally {
            // 释放分布式锁
            stringRedisTemplate.delete(lockKey);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reviewApplication(Long employerId, Long applicationId, Integer status, String rejectReason) {
        Application application = applicationMapper.selectById(applicationId);
        if (application == null) {
            throw new BusinessException("申请不存在");
        }

        // 校验雇主是否拥有该岗位
        JobPost jobPost = jobPostMapper.selectById(application.getJobId());
        if (jobPost == null) {
            throw new BusinessException("岗位不存在");
        }
        if (!jobPost.getPublisherId().equals(employerId)) {
            throw new BusinessException("无权审核此申请");
        }

        application.setStatus(status);
        application.setReviewTime(new Date());
        if (status == 2) {
            // 拒绝 - 设置拒绝原因
            application.setRejectReason(rejectReason);
        }

        if (status == 3) {
            // 录用 - 自动创建订单，同时使用乐观锁更新岗位已招人数
            // 使用乐观锁更新hired_num，防止并发超卖
            int rows = jobPostMapper.update(null,
                    new LambdaUpdateWrapper<JobPost>()
                            .eq(JobPost::getId, application.getJobId())
                            .eq(JobPost::getVersion, jobPost.getVersion())
                            .setSql("hired_num = hired_num + 1")
                            .set(JobPost::getUpdateTime, application.getReviewTime())
            );
            if (rows == 0) {
                throw new BusinessException("岗位已招满，请刷新后重试");
            }

            OrderRecord orderRecord = new OrderRecord();
            orderRecord.setApplicationId(application.getId());
            orderRecord.setStudentId(application.getApplicantId());
            orderRecord.setEmployerId(employerId);
            orderRecord.setJobId(application.getJobId());
            orderRecord.setAmount(java.math.BigDecimal.ZERO);
            orderRecord.setPayStatus(0); // 待支付
            orderRecord.setSettlementStatus(0); // 待结算
            orderRecordMapper.insert(orderRecord);
        }

        applicationMapper.updateById(application);
    }

    @Override
    public PageResult<ApplicationVO> myApplications(Long studentId, int page, int size) {
        Page<ApplicationVO> pageParam = new Page<>(page, size);
        IPage<ApplicationVO> result = applicationMapper.selectMyApplicationPage(pageParam, studentId);
        return PageResult.of(result);
    }

    @Override
    public PageResult<ApplicationVO> jobApplications(Long employerId, Long jobId, int page, int size) {
        Page<ApplicationVO> pageParam = new Page<>(page, size);
        IPage<ApplicationVO> result;
        if (jobId != null) {
            // 校验雇主是否拥有该岗位
            JobPost jobPost = jobPostMapper.selectById(jobId);
            if (jobPost == null) {
                throw new BusinessException("岗位不存在");
            }
            if (!jobPost.getPublisherId().equals(employerId)) {
                throw new BusinessException("无权查看此岗位的申请");
            }
            result = applicationMapper.selectJobApplicationPage(pageParam, jobId);
        } else {
            // 没有指定岗位时，查询该雇主所有岗位的申请（安全：只返回自己的）
            result = applicationMapper.selectEmployerApplicationPage(pageParam, employerId);
        }
        return PageResult.of(result);
    }

    @Override
    public void cancelApplication(Long studentId, Long applicationId) {
        Application application = applicationMapper.selectById(applicationId);
        if (application == null) {
            throw new BusinessException("申请不存在");
        }
        if (!application.getApplicantId().equals(studentId)) {
            throw new BusinessException("无权取消此申请");
        }
        if (application.getStatus() != 0) {
            throw new BusinessException("只有待审核状态可以取消");
        }
        application.setStatus(4); // 已取消
        application.setReviewTime(new Date());
        applicationMapper.updateById(application);
    }

    @Override
    public PageResult<ApplicationVO> allApplications(int page, int size) {
        Page<ApplicationVO> pageParam = new Page<>(page, size);
        IPage<ApplicationVO> result = applicationMapper.selectAllApplicationPage(pageParam);
        return PageResult.of(result);
    }
}
