package com.campus.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.campus.common.dto.ReviewDTO;
import com.campus.common.entity.OrderRecord;
import com.campus.common.entity.Review;
import com.campus.common.entity.UserProfile;
import com.campus.common.exception.BusinessException;
import com.campus.common.vo.ReviewVO;
import com.campus.order.mapper.OrderRecordMapper;
import com.campus.order.mapper.ReviewMapper;
import com.campus.order.mapper.UserProfileMapper;
import com.campus.order.service.ReviewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 评价服务实现
 */
@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;
    private final OrderRecordMapper orderRecordMapper;
    private final UserProfileMapper userProfileMapper;

    public ReviewServiceImpl(ReviewMapper reviewMapper,
                             OrderRecordMapper orderRecordMapper,
                             UserProfileMapper userProfileMapper) {
        this.reviewMapper = reviewMapper;
        this.orderRecordMapper = orderRecordMapper;
        this.userProfileMapper = userProfileMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitReview(Long reviewerId, Long orderId, ReviewDTO dto) {
        OrderRecord order = orderRecordMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        // 只有已结算状态可以评价
        if (order.getSettlementStatus() != 1) {
            throw new BusinessException("该订单未结算，暂不可评价");
        }
        // 检查评价人是否为该订单的学生或雇主
        if (!order.getStudentId().equals(reviewerId) && !order.getEmployerId().equals(reviewerId)) {
            throw new BusinessException("无权评价此订单");
        }

        // 检查是否已评价
        Long existCount = reviewMapper.selectCount(
                new LambdaQueryWrapper<Review>()
                        .eq(Review::getOrderId, orderId)
                        .eq(Review::getReviewerId, reviewerId)
        );
        if (existCount > 0) {
            throw new BusinessException("您已评价过此订单");
        }

        // 创建评价
        Review review = new Review();
        review.setOrderId(orderId);
        review.setReviewerId(reviewerId);
        review.setTargetId(dto.getTargetId());
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review.setType(dto.getType());
        reviewMapper.insert(review);

        // 使用原子操作更新信用分，防止并发竞态条件
        int scoreChange = dto.getRating() >= 4 ? 2 : (dto.getRating() >= 2 ? 0 : -1);
        userProfileMapper.update(null,
                new LambdaUpdateWrapper<UserProfile>()
                        .eq(UserProfile::getUserId, dto.getTargetId())
                        .setSql("credit_score = GREATEST(0, credit_score + " + scoreChange + ")")
        );

        // 检查是否双方都已评价，双方都评价后才标记为已评价
        Long reviewCount = reviewMapper.selectCount(
                new LambdaQueryWrapper<Review>().eq(Review::getOrderId, orderId)
        );
        if (reviewCount >= 2) {
            order.setSettlementStatus(2); // 双方评价完毕，标记为已评价
            orderRecordMapper.updateById(order);
        }
    }

    @Override
    public List<ReviewVO> getOrderReviews(Long orderId) {
        List<Review> reviews = reviewMapper.selectList(
                new LambdaQueryWrapper<Review>()
                        .eq(Review::getOrderId, orderId)
                        .orderByDesc(Review::getCreateTime)
        );
        return buildReviewVOList(reviews, true);
    }

    @Override
    public List<ReviewVO> getMyReviews(Long userId) {
        List<Review> reviews = reviewMapper.selectList(
                new LambdaQueryWrapper<Review>()
                        .eq(Review::getReviewerId, userId)
                        .orderByDesc(Review::getCreateTime)
        );
        return buildReviewVOList(reviews, false);
    }

    /**
     * 将Review实体列表转换为ReviewVO列表
     *
     * @param reviews          Review实体列表
     * @param includeReviewerName 是否包含评价人姓名（true=订单评价列表需要，false=我的评价列表不需要）
     * @return ReviewVO列表
     */
    private List<ReviewVO> buildReviewVOList(List<Review> reviews, boolean includeReviewerName) {
        if (reviews.isEmpty()) {
            return new ArrayList<>();
        }

        // 批量获取用户名
        List<Long> userIds = new ArrayList<>();
        for (Review review : reviews) {
            userIds.add(review.getReviewerId());
            userIds.add(review.getTargetId());
        }
        List<Long> uniqueUserIds = userIds.stream().distinct().collect(Collectors.toList());

        Map<Long, UserProfile> profileMap = userProfileMapper.selectList(
                new LambdaQueryWrapper<UserProfile>().in(UserProfile::getUserId, uniqueUserIds)
        ).stream().collect(Collectors.toMap(UserProfile::getUserId, p -> p));

        List<ReviewVO> voList = new ArrayList<>();
        for (Review review : reviews) {
            ReviewVO vo = new ReviewVO();
            vo.setId(review.getId());
            vo.setOrderId(review.getOrderId());
            vo.setRating(review.getRating());
            vo.setComment(review.getComment());
            vo.setType(review.getType());
            vo.setCreateTime(review.getCreateTime());

            if (includeReviewerName) {
                UserProfile reviewerProfile = profileMap.get(review.getReviewerId());
                vo.setReviewerName(reviewerProfile != null ? reviewerProfile.getRealName() : "未知用户");
            }

            UserProfile targetProfile = profileMap.get(review.getTargetId());
            vo.setTargetName(targetProfile != null ? targetProfile.getRealName() : "未知用户");

            voList.add(vo);
        }

        return voList;
    }
}
