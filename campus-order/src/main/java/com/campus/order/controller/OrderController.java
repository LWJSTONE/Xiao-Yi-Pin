package com.campus.order.controller;

import com.campus.common.dto.ApplyDTO;
import com.campus.common.dto.ReviewDTO;
import com.campus.common.dto.SettleDTO;
import com.campus.common.exception.BusinessException;
import com.campus.common.result.PageResult;
import com.campus.common.result.R;
import com.campus.common.vo.ApplicationVO;
import com.campus.common.vo.OrderRecordVO;
import com.campus.common.vo.ReviewVO;
import com.campus.order.service.ApplicationService;
import com.campus.order.service.OrderService;
import com.campus.order.service.ReviewService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/api/v1/order")
public class OrderController {

    private final ApplicationService applicationService;
    private final OrderService orderService;
    private final ReviewService reviewService;

    public OrderController(ApplicationService applicationService,
                           OrderService orderService,
                           ReviewService reviewService) {
        this.applicationService = applicationService;
        this.orderService = orderService;
        this.reviewService = reviewService;
    }

    /**
     * 申请岗位
     */
    @PostMapping("/apply/{jobId}")
    public R<Void> applyJob(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role-Type") String roleType,
            @PathVariable Long jobId,
            @Validated @RequestBody ApplyDTO dto) {
        if (!"STUDENT".equals(roleType)) {
            throw new BusinessException(403, "只有学生可以申请岗位");
        }
        applicationService.applyJob(userId, jobId, dto);
        return R.ok();
    }

    /**
     * 审核申请（雇主）
     */
    @PutMapping("/apply/{appId}/review")
    public R<Void> reviewApplication(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role-Type") String roleType,
            @PathVariable Long appId,
            @RequestParam Integer status,
            @RequestParam(required = false) String rejectReason) {
        if (!"EMPLOYER".equals(roleType)) {
            throw new BusinessException(403, "只有雇主可以审核申请");
        }
        applicationService.reviewApplication(userId, appId, status, rejectReason);
        return R.ok();
    }

    /**
     * 查询我的申请
     */
    @GetMapping("/my/applications")
    public R<PageResult<ApplicationVO>> myApplications(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return R.ok(applicationService.myApplications(userId, page, size));
    }

    /**
     * 查询岗位的申请列表
     */
    @GetMapping("/my/jobs/applications")
    public R<PageResult<ApplicationVO>> jobApplications(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) Long jobId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return R.ok(applicationService.jobApplications(userId, jobId, page, size));
    }

    /**
     * 结算订单
     */
    @PostMapping("/{orderId}/settle")
    public R<Void> settleOrder(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role-Type") String roleType,
            @PathVariable Long orderId,
            @Validated @RequestBody SettleDTO dto) {
        if (!"EMPLOYER".equals(roleType)) {
            throw new BusinessException(403, "只有雇主可以结算订单");
        }
        orderService.settleOrder(userId, orderId, dto);
        return R.ok();
    }

    /**
     * 查询我的订单
     */
    @GetMapping("/my/orders")
    public R<PageResult<OrderRecordVO>> myOrders(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return R.ok(orderService.myOrders(userId, page, size));
    }

    /**
     * 提交评价
     */
    @PostMapping("/{orderId}/review")
    public R<Void> submitReview(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long orderId,
            @Validated @RequestBody ReviewDTO dto) {
        reviewService.submitReview(userId, orderId, dto);
        return R.ok();
    }

    /**
     * 取消申请（学生）
     */
    @PutMapping("/apply/{appId}/cancel")
    public R<Void> cancelApplication(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role-Type") String roleType,
            @PathVariable Long appId) {
        if (!"STUDENT".equals(roleType)) {
            throw new BusinessException(403, "只有学生可以取消申请");
        }
        applicationService.cancelApplication(userId, appId);
        return R.ok();
    }

    /**
     * 查询我的评价列表
     */
    @GetMapping("/my/reviews")
    public R<List<ReviewVO>> getMyReviews(@RequestHeader("X-User-Id") Long userId) {
        return R.ok(reviewService.getMyReviews(userId));
    }

    /**
     * 查询订单的评价列表
     */
    @GetMapping("/{orderId}/reviews")
    public R<List<ReviewVO>> getOrderReviews(@PathVariable Long orderId) {
        return R.ok(reviewService.getOrderReviews(orderId));
    }

    /**
     * 查询所有申请列表（管理员）
     */
    @GetMapping("/admin/applications")
    public R<PageResult<ApplicationVO>> allApplications(
            @RequestHeader("X-Role-Type") String roleType,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (!"ADMIN".equals(roleType)) {
            throw new BusinessException(403, "无权限访问");
        }
        return R.ok(applicationService.allApplications(page, size));
    }

    /**
     * 查询所有订单列表（管理员）
     */
    @GetMapping("/admin/orders")
    public R<PageResult<OrderRecordVO>> allOrders(
            @RequestHeader("X-Role-Type") String roleType,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (!"ADMIN".equals(roleType)) {
            throw new BusinessException(403, "无权限访问");
        }
        return R.ok(orderService.allOrders(page, size));
    }
}
