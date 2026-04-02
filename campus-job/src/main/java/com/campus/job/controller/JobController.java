package com.campus.job.controller;

import com.campus.common.dto.AuditDTO;
import com.campus.common.dto.JobPostDTO;
import com.campus.common.dto.JobQueryDTO;
import com.campus.common.exception.BusinessException;
import com.campus.common.result.PageResult;
import com.campus.common.result.R;
import com.campus.common.vo.JobCategoryVO;
import com.campus.common.vo.JobPostVO;
import com.campus.job.service.JobCategoryService;
import com.campus.job.service.JobPostService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 岗位控制器
 */
@RestController
@RequestMapping("/api/v1/job")
public class JobController {

    private final JobPostService jobPostService;
    private final JobCategoryService jobCategoryService;

    public JobController(JobPostService jobPostService, JobCategoryService jobCategoryService) {
        this.jobPostService = jobPostService;
        this.jobCategoryService = jobCategoryService;
    }

    /**
     * 获取岗位分类树
     */
    @GetMapping("/categories")
    public R<List<JobCategoryVO>> getCategoryTree() {
        return R.ok(jobCategoryService.getCategoryTree());
    }

    /**
     * 发布岗位（草稿）
     */
    @PostMapping("/posts")
    public R<Void> publishJob(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role-Type") String roleType,
            @Validated @RequestBody JobPostDTO dto) {
        if (!"EMPLOYER".equals(roleType)) {
            throw new BusinessException(403, "只有雇主可以发布岗位");
        }
        jobPostService.publishJob(userId, dto);
        return R.ok();
    }

    /**
     * 更新岗位
     */
    @PutMapping("/posts/{id}")
    public R<Void> updateJob(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role-Type") String roleType,
            @PathVariable Long id,
            @Validated @RequestBody JobPostDTO dto) {
        if (!"EMPLOYER".equals(roleType)) {
            throw new BusinessException(403, "只有雇主可以修改岗位");
        }
        jobPostService.updateJob(userId, id, dto);
        return R.ok();
    }

    /**
     * 分页查询岗位列表
     */
    @GetMapping("/list")
    public R<PageResult<JobPostVO>> listJobs(JobQueryDTO dto) {
        return R.ok(jobPostService.listJobs(dto));
    }

    /**
     * 获取岗位详情
     */
    @GetMapping("/posts/{id}")
    public R<JobPostVO> getJobDetail(@PathVariable Long id) {
        return R.ok(jobPostService.getJobDetail(id));
    }

    /**
     * 审核岗位（管理员）
     */
    @PutMapping("/posts/{id}/audit")
    public R<Void> auditJob(
            @RequestHeader("X-Role-Type") String roleType,
            @PathVariable Long id,
            @Validated @RequestBody AuditDTO dto) {
        if (!"ADMIN".equals(roleType)) {
            throw new BusinessException(403, "无权审核");
        }
        jobPostService.auditJob(id, dto, roleType);
        return R.ok();
    }

    /**
     * 查询我发布的岗位
     */
    @GetMapping("/my/jobs")
    public R<PageResult<JobPostVO>> getMyJobs(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return R.ok(jobPostService.getMyJobs(userId, page, size));
    }

    /**
     * 下线岗位
     */
    @PutMapping("/posts/{id}/offline")
    public R<Void> offlineJob(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        jobPostService.offlineJob(userId, id);
        return R.ok();
    }

    /**
     * 提交审核
     */
    @PutMapping("/posts/{id}/submit")
    public R<Void> submitAudit(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        jobPostService.submitAudit(userId, id);
        return R.ok();
    }
}
