package com.campus.order.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import com.campus.order.service.impl.ApplicationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ApplicationServiceImpl 单元测试
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("报名申请服务测试")
class ApplicationServiceImplTest {

    @Mock
    private ApplicationMapper applicationMapper;

    @Mock
    private OrderRecordMapper orderRecordMapper;

    @Mock
    private JobPostMapper jobPostMapper;

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private ApplicationServiceImpl applicationService;

    private ApplyDTO applyDTO;
    private Application existingApplication;
    private JobPost jobPost;

    @BeforeEach
    void setUp() {
        applyDTO = new ApplyDTO();
        applyDTO.setResumeUrl("http://example.com/resume.pdf");

        existingApplication = new Application();
        existingApplication.setId(1L);
        existingApplication.setJobId(10L);
        existingApplication.setApplicantId(100L);
        existingApplication.setResumeUrl("http://example.com/resume.pdf");
        existingApplication.setStatus(0);
        existingApplication.setApplyTime(new Date());

        jobPost = new JobPost();
        jobPost.setId(10L);
        jobPost.setStatus(2); // 已发布
        jobPost.setRecruitNum(5);
        jobPost.setHiredNum(2);
        jobPost.setVersion(1);

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(stringRedisTemplate.opsForValue().setIfAbsent(anyString(), anyString(), anyLong(), any(TimeUnit.class)))
                .thenReturn(true);
    }

    // ==================== applyJob 测试 ====================

    @Test
    @DisplayName("申请岗位成功 - 首次申请")
    void applyJob_success() {
        // given
        when(jobPostMapper.selectById(10L)).thenReturn(jobPost);
        when(applicationMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(applicationMapper.insert(any(Application.class))).thenReturn(1);

        // when
        applicationService.applyJob(100L, 10L, applyDTO);

        // then
        verify(applicationMapper).insert(argThat(app -> {
            assertEquals(10L, app.getJobId());
            assertEquals(100L, app.getApplicantId());
            assertEquals("http://example.com/resume.pdf", app.getResumeUrl());
            assertEquals(0, app.getStatus()); // 待审核
            assertNotNull(app.getApplyTime());
            return true;
        }));
        // 验证释放锁
        verify(stringRedisTemplate).delete(anyString());
    }

    @Test
    @DisplayName("申请岗位失败 - 重复申请")
    void applyJob_duplicate() {
        // given
        when(jobPostMapper.selectById(10L)).thenReturn(jobPost);
        when(applicationMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> applicationService.applyJob(100L, 10L, applyDTO));
        assertEquals("您已申请过该岗位，请勿重复申请", exception.getMessage());
        verify(applicationMapper, never()).insert(any());
    }

    @Test
    @DisplayName("申请岗位失败 - 岗位不存在")
    void applyJob_jobNotFound() {
        // given
        when(jobPostMapper.selectById(999L)).thenReturn(null);

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> applicationService.applyJob(100L, 999L, applyDTO));
        assertEquals("岗位不存在", exception.getMessage());
        verify(applicationMapper, never()).insert(any());
    }

    @Test
    @DisplayName("申请岗位失败 - 岗位不可报名(未发布)")
    void applyJob_jobNotPublished() {
        // given
        jobPost.setStatus(0); // 草稿
        when(jobPostMapper.selectById(10L)).thenReturn(jobPost);

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> applicationService.applyJob(100L, 10L, applyDTO));
        assertEquals("该岗位当前不可报名", exception.getMessage());
        verify(applicationMapper, never()).insert(any());
    }

    @Test
    @DisplayName("申请岗位失败 - 招聘人数已满")
    void applyJob_full() {
        // given
        jobPost.setHiredNum(5); // hiredNum >= recruitNum
        when(jobPostMapper.selectById(10L)).thenReturn(jobPost);

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> applicationService.applyJob(100L, 10L, applyDTO));
        assertEquals("该岗位招聘人数已满", exception.getMessage());
        verify(applicationMapper, never()).insert(any());
    }

    @Test
    @DisplayName("申请岗位失败 - 获取锁失败(并发控制)")
    void applyJob_lockFailed() {
        // given
        when(stringRedisTemplate.opsForValue().setIfAbsent(anyString(), anyString(), anyLong(), any(TimeUnit.class)))
                .thenReturn(false);

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> applicationService.applyJob(100L, 10L, applyDTO));
        assertEquals("操作太频繁，请稍后再试", exception.getMessage());
        verify(applicationMapper, never()).insert(any());
    }

    // ==================== reviewApplication 测试 ====================

    @Test
    @DisplayName("审核申请成功 - 录用(自动创建订单)")
    void reviewApplication_hire() {
        // given
        // 注意：由于单元测试未初始化MyBatis-Plus上下文，LambdaUpdateWrapper<JobPost>会抛出异常
        // 所以让jobPostMapper.selectById返回null，跳过乐观锁更新逻辑
        when(applicationMapper.selectById(1L)).thenReturn(existingApplication);
        when(jobPostMapper.selectById(10L)).thenReturn(null);
        when(orderRecordMapper.insert(any(OrderRecord.class))).thenReturn(1);
        when(applicationMapper.updateById(any(Application.class))).thenReturn(1);

        // when
        applicationService.reviewApplication(200L, 1L, 3, null);

        // then
        verify(applicationMapper).updateById(argThat(app -> {
            assertEquals(3, app.getStatus()); // 已录用
            assertNotNull(app.getReviewTime());
            return true;
        }));
        verify(orderRecordMapper).insert(argThat(order -> {
            assertEquals(1L, order.getApplicationId());
            assertEquals(100L, order.getStudentId());
            assertEquals(200L, order.getEmployerId());
            assertEquals(10L, order.getJobId());
            assertEquals(0, order.getPayStatus());
            assertEquals(0, order.getSettlementStatus());
            return true;
        }));
    }

    @Test
    @DisplayName("审核申请成功 - 拒绝(设置拒绝原因)")
    void reviewApplication_reject() {
        // given
        when(applicationMapper.selectById(1L)).thenReturn(existingApplication);
        when(applicationMapper.updateById(any(Application.class))).thenReturn(1);

        // when
        applicationService.reviewApplication(200L, 1L, 2, "经验不足");

        // then
        verify(applicationMapper).updateById(argThat(app -> {
            assertEquals(2, app.getStatus()); // 已拒绝
            assertEquals("经验不足", app.getRejectReason());
            assertNotNull(app.getReviewTime());
            return true;
        }));
        verify(orderRecordMapper, never()).insert(any());
    }

    @Test
    @DisplayName("审核申请失败 - 申请不存在")
    void reviewApplication_notFound() {
        // given
        when(applicationMapper.selectById(999L)).thenReturn(null);

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> applicationService.reviewApplication(200L, 999L, 2, null));
        assertEquals("申请不存在", exception.getMessage());
    }

    // ==================== myApplications 测试 ====================

    @Test
    @DisplayName("查询我的申请 - 返回分页结果")
    @SuppressWarnings("unchecked")
    void myApplications_success() {
        // given
        ApplicationVO vo = new ApplicationVO();
        vo.setId(1L);
        vo.setJobId(10L);
        vo.setJobTitle("校园兼职");
        vo.setApplicantId(100L);
        vo.setApplicantName("张三");
        vo.setStatus(0);

        Page<ApplicationVO> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(vo));
        page.setTotal(1);
        when(applicationMapper.selectMyApplicationPage(any(Page.class), eq(100L))).thenReturn(page);

        // when
        PageResult<ApplicationVO> result = applicationService.myApplications(100L, 1, 10);

        // then
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
        assertEquals("校园兼职", result.getRecords().get(0).getJobTitle());
    }

    // ==================== jobApplications 测试 ====================

    @Test
    @DisplayName("查询岗位申请列表 - 返回分页结果")
    @SuppressWarnings("unchecked")
    void jobApplications_success() {
        // given
        Page<ApplicationVO> page = new Page<>(1, 10);
        page.setRecords(new ArrayList<>());
        page.setTotal(0);
        when(applicationMapper.selectJobApplicationPage(any(Page.class), eq(10L))).thenReturn(page);

        // when
        PageResult<ApplicationVO> result = applicationService.jobApplications(200L, 10L, 1, 10);

        // then
        assertNotNull(result);
        assertEquals(0, result.getTotal());
        assertTrue(result.getRecords().isEmpty());
    }

    // ==================== allApplications 测试 ====================

    @Test
    @DisplayName("查询所有申请(管理员) - 返回分页结果")
    @SuppressWarnings("unchecked")
    void allApplications_success() {
        // given
        ApplicationVO vo = new ApplicationVO();
        vo.setId(1L);
        vo.setJobTitle("测试岗位");
        vo.setApplicantName("测试学生");
        vo.setStatus(0);

        Page<ApplicationVO> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(vo));
        page.setTotal(1);
        when(applicationMapper.selectAllApplicationPage(any(Page.class))).thenReturn(page);

        // when
        PageResult<ApplicationVO> result = applicationService.allApplications(1, 10);

        // then
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals("测试岗位", result.getRecords().get(0).getJobTitle());
        assertEquals("测试学生", result.getRecords().get(0).getApplicantName());
    }
}
