package com.campus.job.service;

import com.campus.common.dto.AuditDTO;
import com.campus.common.dto.JobPostDTO;
import com.campus.common.entity.JobPost;
import com.campus.common.exception.BusinessException;
import com.campus.common.vo.JobPostVO;
import com.campus.job.mapper.JobPostMapper;
import com.campus.job.service.impl.JobPostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * JobPostServiceImpl 单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("岗位服务测试")
class JobPostServiceImplTest {

    @Mock
    private JobPostMapper jobPostMapper;

    @InjectMocks
    private JobPostServiceImpl jobPostService;

    private JobPostDTO jobPostDTO;
    private JobPost existingJobPost;

    @BeforeEach
    void setUp() {
        jobPostDTO = new JobPostDTO();
        jobPostDTO.setCategoryId(1L);
        jobPostDTO.setTitle("校园兼职-前台接待");
        jobPostDTO.setDescription("负责前台接待工作");
        jobPostDTO.setLocation("北京大学");
        jobPostDTO.setSalaryType("HOURLY");
        jobPostDTO.setSalaryAmount(new BigDecimal("50.00"));
        jobPostDTO.setStartTime(new Date());
        jobPostDTO.setEndTime(new Date(System.currentTimeMillis() + 86400000L));
        jobPostDTO.setRecruitNum(5);

        existingJobPost = new JobPost();
        existingJobPost.setId(100L);
        existingJobPost.setPublisherId(1L);
        existingJobPost.setCategoryId(1L);
        existingJobPost.setTitle("原有岗位");
        existingJobPost.setStatus(0); // 草稿
        existingJobPost.setAuditStatus(0);
        existingJobPost.setHiredNum(0);
    }

    // ==================== publishJob 测试 ====================

    @Test
    @DisplayName("发布岗位成功 - 创建草稿状态岗位")
    void publishJob_success() {
        // given
        when(jobPostMapper.insert(any(JobPost.class))).thenReturn(1);

        // when
        jobPostService.publishJob(1L, jobPostDTO);

        // then
        verify(jobPostMapper).insert(argThat(job -> {
            assertEquals(1L, job.getPublisherId());
            assertEquals(0, job.getStatus()); // 草稿
            assertEquals(0, job.getAuditStatus());
            assertEquals(0, job.getHiredNum());
            assertEquals("校园兼职-前台接待", job.getTitle());
            return true;
        }));
    }

    // ==================== updateJob 测试 ====================

    @Test
    @DisplayName("更新岗位成功 - 更新自己的草稿岗位")
    void updateJob_success() {
        // given
        existingJobPost.setStatus(0); // 草稿
        when(jobPostMapper.selectById(100L)).thenReturn(existingJobPost);
        when(jobPostMapper.updateById(any(JobPost.class))).thenReturn(1);

        // when
        jobPostService.updateJob(1L, 100L, jobPostDTO);

        // then
        verify(jobPostMapper).updateById(argThat(job -> {
            assertEquals("校园兼职-前台接待", job.getTitle());
            assertEquals("北京大学", job.getLocation());
            assertEquals(new BigDecimal("50.00"), job.getSalaryAmount());
            return true;
        }));
    }

    @Test
    @DisplayName("更新岗位成功 - 审核拒绝状态可以修改")
    void updateJob_rejectedJob() {
        // given
        existingJobPost.setStatus(1);
        existingJobPost.setAuditStatus(2); // 审核拒绝
        when(jobPostMapper.selectById(100L)).thenReturn(existingJobPost);
        when(jobPostMapper.updateById(any(JobPost.class))).thenReturn(1);

        // when
        jobPostService.updateJob(1L, 100L, jobPostDTO);

        // then
        verify(jobPostMapper).updateById(any(JobPost.class));
    }

    @Test
    @DisplayName("更新岗位失败 - 不是自己的岗位")
    void updateJob_notOwner() {
        // given
        when(jobPostMapper.selectById(100L)).thenReturn(existingJobPost);

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> jobPostService.updateJob(2L, 100L, jobPostDTO));
        assertEquals("无权修改此岗位", exception.getMessage());
    }

    @Test
    @DisplayName("更新岗位失败 - 岗位不存在")
    void updateJob_notFound() {
        // given
        when(jobPostMapper.selectById(999L)).thenReturn(null);

        // when & then
        assertThrows(BusinessException.class, () -> jobPostService.updateJob(1L, 999L, jobPostDTO));
    }

    @Test
    @DisplayName("更新岗位失败 - 当前状态不允许修改(已发布)")
    void updateJob_wrongStatus() {
        // given
        existingJobPost.setStatus(2); // 已发布
        existingJobPost.setAuditStatus(1);
        when(jobPostMapper.selectById(100L)).thenReturn(existingJobPost);

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> jobPostService.updateJob(1L, 100L, jobPostDTO));
        assertEquals("当前状态不允许修改", exception.getMessage());
    }

    // ==================== submitAudit 测试 ====================

    @Test
    @DisplayName("提交审核成功 - 草稿状态提交审核")
    void submitAudit_success() {
        // given
        existingJobPost.setStatus(0); // 草稿
        when(jobPostMapper.selectById(100L)).thenReturn(existingJobPost);
        when(jobPostMapper.updateById(any(JobPost.class))).thenReturn(1);

        // when
        jobPostService.submitAudit(1L, 100L);

        // then
        verify(jobPostMapper).updateById(argThat(job -> {
            assertEquals(1, job.getStatus()); // 审核中
            assertEquals(0, job.getAuditStatus());
            return true;
        }));
    }

    @Test
    @DisplayName("提交审核失败 - 非草稿状态")
    void submitAudit_notDraft() {
        // given
        existingJobPost.setStatus(1); // 审核中
        when(jobPostMapper.selectById(100L)).thenReturn(existingJobPost);

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> jobPostService.submitAudit(1L, 100L));
        assertEquals("只有草稿状态可以提交审核", exception.getMessage());
    }

    @Test
    @DisplayName("提交审核失败 - 不是自己的岗位")
    void submitAudit_notOwner() {
        // given
        existingJobPost.setStatus(0);
        when(jobPostMapper.selectById(100L)).thenReturn(existingJobPost);

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> jobPostService.submitAudit(2L, 100L));
        assertEquals("无权操作此岗位", exception.getMessage());
    }

    // ==================== auditJob 测试 ====================

    @Test
    @DisplayName("审核岗位成功 - 审核通过")
    void auditJob_approved() {
        // given
        existingJobPost.setStatus(1); // 审核中
        when(jobPostMapper.selectById(100L)).thenReturn(existingJobPost);
        when(jobPostMapper.updateById(any(JobPost.class))).thenReturn(1);

        AuditDTO dto = new AuditDTO();
        dto.setAuditStatus(1); // 通过
        dto.setAuditRemark("审核通过");

        // when
        jobPostService.auditJob(100L, dto, "ADMIN");

        // then
        verify(jobPostMapper).updateById(argThat(job -> {
            assertEquals(1, job.getAuditStatus());
            assertEquals(2, job.getStatus()); // 已发布
            assertEquals("审核通过", job.getAuditRemark());
            return true;
        }));
    }

    @Test
    @DisplayName("审核岗位成功 - 审核拒绝")
    void auditJob_rejected() {
        // given
        existingJobPost.setStatus(1); // 审核中
        when(jobPostMapper.selectById(100L)).thenReturn(existingJobPost);
        when(jobPostMapper.updateById(any(JobPost.class))).thenReturn(1);

        AuditDTO dto = new AuditDTO();
        dto.setAuditStatus(2); // 拒绝
        dto.setAuditRemark("信息不完整");

        // when
        jobPostService.auditJob(100L, dto, "ADMIN");

        // then
        verify(jobPostMapper).updateById(argThat(job -> {
            assertEquals(2, job.getAuditStatus());
            assertEquals(0, job.getStatus()); // 改回草稿
            assertEquals("信息不完整", job.getAuditRemark());
            return true;
        }));
    }

    @Test
    @DisplayName("审核岗位失败 - 非管理员角色")
    void auditJob_notAdmin() {
        // given
        AuditDTO dto = new AuditDTO();
        dto.setAuditStatus(1);
        dto.setAuditRemark("通过");

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> jobPostService.auditJob(100L, dto, "STUDENT"));
        assertEquals(403, exception.getCode());
        assertEquals("无权审核", exception.getMessage());
    }

    @Test
    @DisplayName("审核岗位失败 - 岗位不在审核中状态")
    void auditJob_notInReview() {
        // given
        existingJobPost.setStatus(0); // 草稿
        when(jobPostMapper.selectById(100L)).thenReturn(existingJobPost);

        AuditDTO dto = new AuditDTO();
        dto.setAuditStatus(1);
        dto.setAuditRemark("通过");

        // when & then
        assertThrows(BusinessException.class, () -> jobPostService.auditJob(100L, dto, "ADMIN"));
    }

    // ==================== offlineJob 测试 ====================

    @Test
    @DisplayName("下线岗位成功 - 已发布状态可以下线")
    void offlineJob_success() {
        // given
        existingJobPost.setStatus(2); // 已发布
        when(jobPostMapper.selectById(100L)).thenReturn(existingJobPost);
        when(jobPostMapper.updateById(any(JobPost.class))).thenReturn(1);

        // when
        jobPostService.offlineJob(1L, 100L);

        // then
        verify(jobPostMapper).updateById(argThat(job -> {
            assertEquals(4, job.getStatus()); // 已下线
            return true;
        }));
    }

    @Test
    @DisplayName("下线岗位失败 - 非已发布状态")
    void offlineJob_wrongStatus() {
        // given
        existingJobPost.setStatus(0); // 草稿
        when(jobPostMapper.selectById(100L)).thenReturn(existingJobPost);

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> jobPostService.offlineJob(1L, 100L));
        assertEquals("只有已发布状态可以下线", exception.getMessage());
    }

    @Test
    @DisplayName("下线岗位失败 - 不是自己的岗位")
    void offlineJob_notOwner() {
        // given
        existingJobPost.setStatus(2);
        when(jobPostMapper.selectById(100L)).thenReturn(existingJobPost);

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> jobPostService.offlineJob(2L, 100L));
        assertEquals("无权操作此岗位", exception.getMessage());
    }

    // ==================== getJobDetail 测试 ====================

    @Test
    @DisplayName("获取岗位详情成功")
    void getJobDetail_success() {
        // given
        JobPostVO vo = new JobPostVO();
        vo.setId(100L);
        vo.setTitle("测试岗位");
        when(jobPostMapper.selectJobDetail(100L)).thenReturn(vo);

        // when
        JobPostVO result = jobPostService.getJobDetail(100L);

        // then
        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("测试岗位", result.getTitle());
    }

    @Test
    @DisplayName("获取岗位详情失败 - 岗位不存在")
    void getJobDetail_notFound() {
        // given
        when(jobPostMapper.selectJobDetail(999L)).thenReturn(null);

        // when & then
        assertThrows(BusinessException.class, () -> jobPostService.getJobDetail(999L));
    }
}
