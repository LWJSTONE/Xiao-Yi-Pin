package com.campus.order.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.common.dto.ReviewDTO;
import com.campus.common.entity.OrderRecord;
import com.campus.common.entity.Review;
import com.campus.common.entity.UserProfile;
import com.campus.common.exception.BusinessException;
import com.campus.common.vo.ReviewVO;
import com.campus.order.mapper.OrderRecordMapper;
import com.campus.order.mapper.ReviewMapper;
import com.campus.order.mapper.UserProfileMapper;
import com.campus.order.service.impl.ReviewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ReviewServiceImpl 单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("评价服务测试")
class ReviewServiceImplTest {

    @Mock
    private ReviewMapper reviewMapper;

    @Mock
    private OrderRecordMapper orderRecordMapper;

    @Mock
    private UserProfileMapper userProfileMapper;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private OrderRecord existingOrder;
    private ReviewDTO reviewDTO;
    private UserProfile reviewerProfile;
    private UserProfile targetProfile;

    @BeforeEach
    void setUp() {
        existingOrder = new OrderRecord();
        existingOrder.setId(1L);
        existingOrder.setStudentId(10L);
        existingOrder.setEmployerId(20L);
        existingOrder.setJobId(30L);
        existingOrder.setSettlementStatus(1);

        reviewDTO = new ReviewDTO();
        reviewDTO.setTargetId(20L);
        reviewDTO.setRating(5);
        reviewDTO.setComment("\u975e\u597d");
        reviewDTO.setType("STUDENT_TO_EMPLOYER");

        reviewerProfile = new UserProfile();
        reviewerProfile.setId(1L);
        reviewerProfile.setUserId(10L);
        reviewerProfile.setRealName("\u5f20\u4e09");
        reviewerProfile.setCreditScore(100);

        targetProfile = new UserProfile();
        targetProfile.setId(2L);
        targetProfile.setUserId(20L);
        targetProfile.setRealName("\u674e\u56db");
        targetProfile.setCreditScore(100);
    }

    @Test
    @DisplayName("提交评价成功 - \u5b66\u751f\u8bc4\u4ef7\u96c7\u4e3b(\u9ad8\u8bc4\u5206\u52a0\u5206)")
    void submitReview_success_highRating() {
        when(orderRecordMapper.selectById(1L)).thenReturn(existingOrder);
        when(reviewMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(reviewMapper.insert(any(Review.class))).thenReturn(1);
        when(orderRecordMapper.updateById(any(OrderRecord.class))).thenReturn(1);
        when(userProfileMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(targetProfile);
        when(userProfileMapper.updateById(any(UserProfile.class))).thenReturn(1);

        reviewService.submitReview(10L, 1L, reviewDTO);

        ArgumentCaptor<Review> reviewCaptor = ArgumentCaptor.forClass(Review.class);
        verify(reviewMapper).insert(reviewCaptor.capture());
        Review savedReview = reviewCaptor.getValue();
        assertEquals(1L, savedReview.getOrderId());
        assertEquals(10L, savedReview.getReviewerId());
        assertEquals(20L, savedReview.getTargetId());
        assertEquals(5, savedReview.getRating());
        assertNotNull(savedReview.getCreateTime());

        ArgumentCaptor<OrderRecord> orderCaptor = ArgumentCaptor.forClass(OrderRecord.class);
        verify(orderRecordMapper).updateById(orderCaptor.capture());
        assertEquals(2, orderCaptor.getValue().getSettlementStatus());

        ArgumentCaptor<UserProfile> profileCaptor = ArgumentCaptor.forClass(UserProfile.class);
        verify(userProfileMapper).updateById(profileCaptor.capture());
        assertEquals(102, profileCaptor.getValue().getCreditScore());
    }

    @Test
    @DisplayName("提交评价成功 - \u4f4e\u8bc4\u5206\u51cf\u5206")
    void submitReview_success_lowRating() {
        reviewDTO.setRating(1);
        reviewDTO.setComment("\u5f88\u5dee");
        when(orderRecordMapper.selectById(1L)).thenReturn(existingOrder);
        when(reviewMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(reviewMapper.insert(any(Review.class))).thenReturn(1);
        when(orderRecordMapper.updateById(any(OrderRecord.class))).thenReturn(1);
        when(userProfileMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(targetProfile);
        when(userProfileMapper.updateById(any(UserProfile.class))).thenReturn(1);

        reviewService.submitReview(10L, 1L, reviewDTO);

        ArgumentCaptor<UserProfile> profileCaptor = ArgumentCaptor.forClass(UserProfile.class);
        verify(userProfileMapper).updateById(profileCaptor.capture());
        assertEquals(99, profileCaptor.getValue().getCreditScore());
    }

    @Test
    @DisplayName("提交评价成功 - \u4e2d\u7b49\u8bc4\u5206\u4e0d\u6539\u53d8\u4fe1\u7528\u5206")
    void submitReview_success_mediumRating() {
        reviewDTO.setRating(3);
        reviewDTO.setComment("\u4e00\u822c");
        when(orderRecordMapper.selectById(1L)).thenReturn(existingOrder);
        when(reviewMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(reviewMapper.insert(any(Review.class))).thenReturn(1);
        when(orderRecordMapper.updateById(any(OrderRecord.class))).thenReturn(1);
        when(userProfileMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(targetProfile);
        when(userProfileMapper.updateById(any(UserProfile.class))).thenReturn(1);

        reviewService.submitReview(10L, 1L, reviewDTO);

        ArgumentCaptor<UserProfile> profileCaptor = ArgumentCaptor.forClass(UserProfile.class);
        verify(userProfileMapper).updateById(profileCaptor.capture());
        assertEquals(100, profileCaptor.getValue().getCreditScore());
    }

    @Test
    @DisplayName("提交评价成功 - \u96c7\u4e3b\u8bc4\u4ef7\u5b66\u751f")
    void submitReview_success_employerReview() {
        reviewDTO.setTargetId(10L);
        reviewDTO.setType("EMPLOYER_TO_STUDENT");
        when(orderRecordMapper.selectById(1L)).thenReturn(existingOrder);
        when(reviewMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(reviewMapper.insert(any(Review.class))).thenReturn(1);
        when(orderRecordMapper.updateById(any(OrderRecord.class))).thenReturn(1);
        when(userProfileMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(reviewerProfile);
        when(userProfileMapper.updateById(any(UserProfile.class))).thenReturn(1);

        reviewService.submitReview(20L, 1L, reviewDTO);

        ArgumentCaptor<Review> reviewCaptor = ArgumentCaptor.forClass(Review.class);
        verify(reviewMapper).insert(reviewCaptor.capture());
        assertEquals(20L, reviewCaptor.getValue().getReviewerId());
    }

    @Test
    @DisplayName("提交评价失败 - \u8ba2\u5355\u4e0d\u5b58\u5728")
    void submitReview_orderNotFound() {
        when(orderRecordMapper.selectById(999L)).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> reviewService.submitReview(10L, 999L, reviewDTO));
        assertEquals("\u8ba2\u5355\u4e0d\u5b58\u5728", exception.getMessage());
    }

    @Test
    @DisplayName("提交评价失败 - \u8ba2\u5355\u672a\u7ed3\u7b97")
    void submitReview_notSettled() {
        existingOrder.setSettlementStatus(0);
        when(orderRecordMapper.selectById(1L)).thenReturn(existingOrder);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> reviewService.submitReview(10L, 1L, reviewDTO));
        assertEquals("\u8be5\u8ba2\u5355\u672a\u7ed3\u7b97\uff0c\u6682\u4e0d\u53ef\u8bc4\u4ef7", exception.getMessage());
    }

    @Test
    @DisplayName("提交评价失败 - \u65e0\u6743\u8bc4\u4ef7\u6b64\u8ba2\u5355")
    void submitReview_notAuthorized() {
        when(orderRecordMapper.selectById(1L)).thenReturn(existingOrder);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> reviewService.submitReview(99L, 1L, reviewDTO));
        assertEquals("\u65e0\u6743\u8bc4\u4ef7\u6b64\u8ba2\u5355", exception.getMessage());
    }

    @Test
    @DisplayName("提交评价失败 - \u91cd\u590d\u8bc4\u4ef7")
    void submitReview_duplicateReview() {
        when(orderRecordMapper.selectById(1L)).thenReturn(existingOrder);
        when(reviewMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> reviewService.submitReview(10L, 1L, reviewDTO));
        assertEquals("\u60a8\u5df2\u8bc4\u4ef7\u8fc7\u6b64\u8ba2\u5355", exception.getMessage());
    }

    @Test
    @DisplayName("\u67e5\u8be2\u8ba2\u5355\u8bc4\u4ef7\u5217\u8868 - \u8fd4\u56de\u8bc4\u4ef7\u5217\u8868")
    @SuppressWarnings("unchecked")
    void getOrderReviews_success() {
        Review review = new Review();
        review.setId(1L);
        review.setOrderId(1L);
        review.setReviewerId(10L);
        review.setTargetId(20L);
        review.setRating(5);
        review.setComment("\u975e\u597d");
        review.setType("STUDENT_TO_EMPLOYER");
        review.setCreateTime(new Date());

        when(reviewMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.singletonList(review));

        List<UserProfile> profiles = new ArrayList<>();
        profiles.add(reviewerProfile);
        profiles.add(targetProfile);
        when(userProfileMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(profiles);

        List<ReviewVO> result = reviewService.getOrderReviews(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        ReviewVO vo = result.get(0);
        assertEquals(1L, vo.getId());
        assertEquals(1L, vo.getOrderId());
        assertEquals(5, vo.getRating());
        assertEquals("\u975e\u597d", vo.getComment());
        assertEquals("\u5f20\u4e09", vo.getReviewerName());
        assertEquals("\u674e\u56db", vo.getTargetName());
    }

    @Test
    @DisplayName("\u67e5\u8be2\u8ba2\u5355\u8bc4\u4ef7\u5217\u8868 - \u65e0\u8bc4\u4ef7\u8fd4\u56de\u7a7a\u5217\u8868")
    @SuppressWarnings("unchecked")
    void getOrderReviews_empty() {
        when(reviewMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        List<ReviewVO> result = reviewService.getOrderReviews(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("\u67e5\u8be2\u8ba2\u5355\u8bc4\u4ef7\u5217\u8868 - \u7528\u6237Profile\u4e0d\u5b58\u5728\u663e\u793a\u672a\u77e5\u7528\u6237")
    @SuppressWarnings("unchecked")
    void getOrderReviews_unknownUser() {
        Review review = new Review();
        review.setId(1L);
        review.setOrderId(1L);
        review.setReviewerId(10L);
        review.setTargetId(20L);
        review.setRating(4);
        review.setComment("\u4e0d\u9519");
        review.setType("STUDENT_TO_EMPLOYER");
        review.setCreateTime(new Date());

        when(reviewMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.singletonList(review));
        when(userProfileMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        List<ReviewVO> result = reviewService.getOrderReviews(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("\u672a\u77e5\u7528\u6237", result.get(0).getReviewerName());
        assertEquals("\u672a\u77e5\u7528\u6237", result.get(0).getTargetName());
    }
}
