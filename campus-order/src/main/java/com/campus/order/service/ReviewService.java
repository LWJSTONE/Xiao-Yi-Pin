package com.campus.order.service;

import com.campus.common.dto.ReviewDTO;
import com.campus.common.vo.ReviewVO;

import java.util.List;

/**
 * 评价服务接口
 */
public interface ReviewService {

    /**
     * 提交评价
     */
    void submitReview(Long reviewerId, Long orderId, ReviewDTO dto);

    /**
     * 查询订单的评价列表
     */
    List<ReviewVO> getOrderReviews(Long orderId);
}
