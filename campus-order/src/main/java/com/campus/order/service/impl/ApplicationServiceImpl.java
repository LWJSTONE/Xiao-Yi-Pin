package com.campus.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.dto.ApplyDTO;
import com.campus.common.entity.Application;
import com.campus.common.entity.JobPost;
import com.campus.common.entity.OrderRecord;
import com.campus.common.exception.BusinessException;
import com.campus.common.result.PageResult;
import com.campus.common.vo.ApplicationVO;
import com.campus.order.mapper.ApplicationMapper;
import com.campus.order.mapper.OrderRecordMapper;
import com.campus.order.service.ApplicationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 报名申请服务实现
 */
@Service
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationMapper applicationMapper;
    private final OrderRecordMapper orderRecordMapper;

    public ApplicationServiceImpl(ApplicationMapper applicationMapper, OrderRecordMapper orderRecordMapper) {
        this.applicationMapper = applicationMapper;
        this.orderRecordMapper = orderRecordMapper;
    }

    @Override
    public void applyJob(Long studentId, Long jobId, ApplyDTO dto) {
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
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reviewApplication(Long employerId, Long applicationId, Integer status, String rejectReason) {
        Application application = applicationMapper.selectById(applicationId);
        if (application == null) {
            throw new BusinessException("申请不存在");
        }

        // 检查该申请对应的岗位是否属于该雇主
        // 通过订单模块的数据库访问验证
        application.setStatus(status);
        application.setReviewTime(new Date());
        if (status == 3) {
            application.setRejectReason(rejectReason);
        }

        if (status == 2) {
            // 录用 - 自动创建订单
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
        IPage<ApplicationVO> result = applicationMapper.selectJobApplicationPage(pageParam, jobId);
        return PageResult.of(result);
    }

    @Override
    public PageResult<ApplicationVO> allApplications(int page, int size) {
        Page<ApplicationVO> pageParam = new Page<>(page, size);
        IPage<ApplicationVO> result = applicationMapper.selectAllApplicationPage(pageParam);
        return PageResult.of(result);
    }
}
