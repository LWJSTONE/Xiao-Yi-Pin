package com.campus.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.dto.UserProfileDTO;
import com.campus.common.dto.VerifyApplyDTO;
import com.campus.common.entity.SysUser;
import com.campus.common.entity.UserProfile;
import com.campus.common.exception.BusinessException;
import com.campus.common.result.PageResult;
import com.campus.common.vo.UserProfileVO;
import com.campus.user.mapper.SysUserMapper;
import com.campus.user.mapper.UserProfileMapper;
import com.campus.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * UserServiceImpl 单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("用户服务测试")
class UserServiceImplTest {

    @Mock
    private SysUserMapper sysUserMapper;

    @Mock
    private UserProfileMapper userProfileMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private SysUser sysUser;
    private UserProfile userProfile;

    @BeforeEach
    void setUp() {
        sysUser = new SysUser();
        sysUser.setId(1L);
        sysUser.setUsername("testuser");
        sysUser.setPasswordHash("encodedPassword");
        sysUser.setRoleType("STUDENT");
        sysUser.setStatus(1);
        sysUser.setPhone("13812345678");
        sysUser.setEmail("testuser@example.com");

        userProfile = new UserProfile();
        userProfile.setId(10L);
        userProfile.setUserId(1L);
        userProfile.setRealName("张三");
        userProfile.setGender(1);
        userProfile.setUniversity("北京大学");
        userProfile.setMajor("计算机科学");
        userProfile.setGrade("大三");
        userProfile.setBalance(new BigDecimal("100.00"));
        userProfile.setCreditScore(100);
        userProfile.setAvatarUrl("http://example.com/avatar.png");
        userProfile.setVerifiedStatus(0);
    }

    // ==================== getMyProfile 测试 ====================

    @Test
    @DisplayName("获取我的资料成功 - 用户和资料都存在")
    void getMyProfile_success() {
        // given
        when(sysUserMapper.selectById(1L)).thenReturn(sysUser);
        when(userProfileMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(userProfile);

        // when
        UserProfileVO result = userService.getMyProfile(1L);

        // then
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals("testuser", result.getUsername());
        assertEquals("STUDENT", result.getRoleType());
        assertEquals(1, result.getStatus());
        assertEquals("张三", result.getRealName());
        assertEquals("北京大学", result.getUniversity());
        assertEquals("计算机科学", result.getMajor());
        assertEquals("大三", result.getGrade());
        assertEquals(new BigDecimal("100.00"), result.getBalance());
        assertEquals(100, result.getCreditScore());
        assertEquals("http://example.com/avatar.png", result.getAvatarUrl());
        assertEquals(0, result.getVerifiedStatus());
        // 验证脱敏
        assertEquals("138****5678", result.getPhone());
        assertEquals("te***@example.com", result.getEmail());
    }

    @Test
    @DisplayName("获取我的资料成功 - 无Profile记录只返回用户基本信息")
    void getMyProfile_noProfile() {
        // given
        when(sysUserMapper.selectById(1L)).thenReturn(sysUser);
        when(userProfileMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        // when
        UserProfileVO result = userService.getMyProfile(1L);

        // then
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals("testuser", result.getUsername());
        assertNull(result.getRealName());
        assertNull(result.getUniversity());
    }

    @Test
    @DisplayName("获取我的资料失败 - 用户不存在")
    void getMyProfile_userNotFound() {
        // given
        when(sysUserMapper.selectById(999L)).thenReturn(null);

        // when & then
        assertThrows(BusinessException.class, () -> userService.getMyProfile(999L));
    }

    // ==================== updateMyProfile 测试 ====================

    @Test
    @DisplayName("更新我的资料 - 首次创建Profile")
    void updateMyProfile_create() {
        // given
        UserProfileDTO dto = new UserProfileDTO();
        dto.setRealName("李四");
        dto.setGender(2);
        dto.setUniversity("清华大学");

        when(userProfileMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(sysUserMapper.selectById(1L)).thenReturn(sysUser);
        when(userProfileMapper.insert(any(UserProfile.class))).thenReturn(1);

        // when
        UserProfileVO result = userService.updateMyProfile(1L, dto);

        // then
        assertNotNull(result);
        verify(userProfileMapper).insert(any(UserProfile.class));
    }

    @Test
    @DisplayName("更新我的资料 - 更新已有Profile")
    void updateMyProfile_update() {
        // given
        UserProfileDTO dto = new UserProfileDTO();
        dto.setRealName("王五");
        dto.setMajor("软件工程");

        when(userProfileMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(userProfile);
        when(sysUserMapper.selectById(1L)).thenReturn(sysUser);
        when(userProfileMapper.updateById(any(UserProfile.class))).thenReturn(1);

        // when
        UserProfileVO result = userService.updateMyProfile(1L, dto);

        // then
        assertNotNull(result);
        verify(userProfileMapper).updateById(any(UserProfile.class));
        verify(userProfileMapper, never()).insert(any(UserProfile.class));
    }

    // ==================== applyVerify 测试 ====================

    @Test
    @DisplayName("申请实名认证成功 - 设置审核中状态")
    void applyVerify_success() {
        // given
        VerifyApplyDTO dto = new VerifyApplyDTO();
        dto.setRealName("张三");
        dto.setIdCard("110101199001011234");
        dto.setIdCardImage("http://example.com/id.jpg");
        dto.setVerifyType("ID_CARD");

        when(userProfileMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(userProfile);
        when(userProfileMapper.updateById(any(UserProfile.class))).thenReturn(1);

        // when
        userService.applyVerify(1L, dto);

        // then
        verify(userProfileMapper).updateById(argThat(profile -> {
            assertEquals("张三", profile.getRealName());
            assertEquals(1, profile.getVerifiedStatus());
            assertNotNull(profile.getIdCardHash());
            return true;
        }));
    }

    @Test
    @DisplayName("申请实名认证成功 - 无Profile时自动创建")
    void applyVerify_createProfile() {
        // given
        VerifyApplyDTO dto = new VerifyApplyDTO();
        dto.setRealName("李四");
        dto.setIdCard("110101199001011234");
        dto.setIdCardImage("http://example.com/id.jpg");
        dto.setVerifyType("ID_CARD");

        when(userProfileMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(userProfileMapper.insert(any(UserProfile.class))).thenReturn(1);

        // when
        userService.applyVerify(2L, dto);

        // then
        verify(userProfileMapper).insert(argThat(profile -> {
            assertEquals(2L, profile.getUserId());
            assertEquals("李四", profile.getRealName());
            assertEquals(1, profile.getVerifiedStatus());
            return true;
        }));
    }

    @Test
    @DisplayName("申请实名认证失败 - 已认证用户重复申请")
    void applyVerify_alreadyVerified() {
        // given
        userProfile.setVerifiedStatus(2); // 已认证
        VerifyApplyDTO dto = new VerifyApplyDTO();
        dto.setRealName("张三");
        dto.setIdCard("110101199001011234");
        dto.setIdCardImage("http://example.com/id.jpg");
        dto.setVerifyType("ID_CARD");

        when(userProfileMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(userProfile);

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> userService.applyVerify(1L, dto));
        assertEquals("已完成实名认证，无需重复申请", exception.getMessage());
    }

    // ==================== listUsers 测试 ====================

    @Test
    @DisplayName("分页查询用户列表 - 带关键词搜索")
    @SuppressWarnings("unchecked")
    void listUsers_withKeyword() {
        // given
        Page<SysUser> mockPage = new Page<>(1, 10);
        mockPage.setRecords(java.util.Collections.singletonList(sysUser));
        mockPage.setTotal(1);
        when(sysUserMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(mockPage);
        when(userProfileMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(userProfile);

        // when
        PageResult<UserProfileVO> result = userService.listUsers("test", null, 1, 10);

        // then
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
        assertEquals("testuser", result.getRecords().get(0).getUsername());
    }

    @Test
    @DisplayName("分页查询用户列表 - 空结果")
    @SuppressWarnings("unchecked")
    void listUsers_emptyResult() {
        // given
        Page<SysUser> mockPage = new Page<>(1, 10);
        mockPage.setRecords(java.util.Collections.emptyList());
        mockPage.setTotal(0);
        when(sysUserMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(mockPage);

        // when
        PageResult<UserProfileVO> result = userService.listUsers("nonexistent", null, 1, 10);

        // then
        assertNotNull(result);
        assertEquals(0, result.getTotal());
        assertTrue(result.getRecords().isEmpty());
    }

    // ==================== updateUserStatus 测试 ====================

    @Test
    @DisplayName("更新用户状态成功")
    void updateUserStatus_success() {
        // given
        when(sysUserMapper.selectById(1L)).thenReturn(sysUser);
        when(sysUserMapper.updateById(any(SysUser.class))).thenReturn(1);

        // when
        userService.updateUserStatus(1L, 0);

        // then
        verify(sysUserMapper).updateById(argThat(user -> {
            assertEquals(0, user.getStatus());
            return true;
        }));
    }

    @Test
    @DisplayName("更新用户状态失败 - 用户不存在")
    void updateUserStatus_notFound() {
        // given
        when(sysUserMapper.selectById(999L)).thenReturn(null);

        // when & then
        assertThrows(BusinessException.class, () -> userService.updateUserStatus(999L, 0));
    }

    // ==================== getVerifyStatus 测试 ====================

    @Test
    @DisplayName("获取认证状态 - 有Profile返回认证状态")
    void getVerifyStatus_hasProfile() {
        // given
        userProfile.setVerifiedStatus(1);
        when(userProfileMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(userProfile);

        // when
        Integer status = userService.getVerifyStatus(1L);

        // then
        assertEquals(1, status);
    }

    @Test
    @DisplayName("获取认证状态 - 无Profile返回0")
    void getVerifyStatus_noProfile() {
        // given
        when(userProfileMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        // when
        Integer status = userService.getVerifyStatus(1L);

        // then
        assertEquals(0, status);
    }
}
