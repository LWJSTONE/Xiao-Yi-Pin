package com.campus.user.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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
import com.campus.user.service.UserService;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现
 */
@Service
public class UserServiceImpl implements UserService {

    private final SysUserMapper sysUserMapper;
    private final UserProfileMapper userProfileMapper;

    public UserServiceImpl(SysUserMapper sysUserMapper, UserProfileMapper userProfileMapper) {
        this.sysUserMapper = sysUserMapper;
        this.userProfileMapper = userProfileMapper;
    }

    @Override
    public UserProfileVO getMyProfile(Long userId) {
        SysUser sysUser = sysUserMapper.selectById(userId);
        if (sysUser == null) {
            throw new BusinessException("用户不存在");
        }

        UserProfile profile = userProfileMapper.selectOne(
                new LambdaQueryWrapper<UserProfile>().eq(UserProfile::getUserId, userId)
        );

        UserProfileVO vo = new UserProfileVO();
        vo.setUserId(sysUser.getId());
        vo.setUsername(sysUser.getUsername());
        vo.setRoleType(sysUser.getRoleType());
        vo.setStatus(sysUser.getStatus());
        // 手机号脱敏
        vo.setPhone(maskPhone(sysUser.getPhone()));
        // 邮箱脱敏
        vo.setEmail(maskEmail(sysUser.getEmail()));

        if (profile != null) {
            vo.setRealName(profile.getRealName());
            vo.setGender(profile.getGender());
            vo.setUniversity(profile.getUniversity());
            vo.setMajor(profile.getMajor());
            vo.setGrade(profile.getGrade());
            vo.setBalance(profile.getBalance());
            vo.setCreditScore(profile.getCreditScore());
            vo.setAvatarUrl(profile.getAvatarUrl());
            vo.setVerifiedStatus(profile.getVerifiedStatus());
        }

        return vo;
    }

    @Override
    public UserProfileVO updateMyProfile(Long userId, UserProfileDTO dto) {
        UserProfile profile = userProfileMapper.selectOne(
                new LambdaQueryWrapper<UserProfile>().eq(UserProfile::getUserId, userId)
        );

        if (profile == null) {
            // 首次创建资料
            profile = new UserProfile();
            profile.setUserId(userId);
            profile.setBalance(java.math.BigDecimal.ZERO);
            profile.setCreditScore(100);
            profile.setVerifiedStatus(0);
            BeanUtils.copyProperties(dto, profile);
            userProfileMapper.insert(profile);
        } else {
            // 更新已有资料
            if (dto.getRealName() != null) {
                profile.setRealName(dto.getRealName());
            }
            if (dto.getGender() != null) {
                profile.setGender(dto.getGender());
            }
            if (dto.getUniversity() != null) {
                profile.setUniversity(dto.getUniversity());
            }
            if (dto.getMajor() != null) {
                profile.setMajor(dto.getMajor());
            }
            if (dto.getGrade() != null) {
                profile.setGrade(dto.getGrade());
            }
            if (dto.getAvatarUrl() != null) {
                profile.setAvatarUrl(dto.getAvatarUrl());
            }
            userProfileMapper.updateById(profile);
        }

        return getMyProfile(userId);
    }

    @Override
    public void applyVerify(Long userId, VerifyApplyDTO dto) {
        UserProfile profile = userProfileMapper.selectOne(
                new LambdaQueryWrapper<UserProfile>().eq(UserProfile::getUserId, userId)
        );

        if (profile == null) {
            profile = new UserProfile();
            profile.setUserId(userId);
            profile.setBalance(java.math.BigDecimal.ZERO);
            profile.setCreditScore(100);
        }

        // 检查是否已认证
        if (profile.getVerifiedStatus() != null && profile.getVerifiedStatus() == 2) {
            throw new BusinessException("已完成实名认证，无需重复申请");
        }

        // MD5哈希身份证号
        String idCardHash = DigestUtil.md5Hex(dto.getIdCard());
        profile.setIdCardHash(idCardHash);
        profile.setRealName(dto.getRealName());
        // 设置认证状态为审核中
        profile.setVerifiedStatus(1);

        if (profile.getId() == null) {
            userProfileMapper.insert(profile);
        } else {
            userProfileMapper.updateById(profile);
        }
    }

    @Override
    public Integer getVerifyStatus(Long userId) {
        UserProfile profile = userProfileMapper.selectOne(
                new LambdaQueryWrapper<UserProfile>().eq(UserProfile::getUserId, userId)
        );
        return profile != null ? profile.getVerifiedStatus() : 0;
    }

    @Override
    public PageResult<UserProfileVO> listUsers(String keyword, Integer status, int page, int size) {
        // 先查询sys_user分页
        Page<SysUser> userPage = new Page<>(page, size);
        LambdaQueryWrapper<SysUser> userWrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(keyword)) {
            userWrapper.like(SysUser::getUsername, keyword)
                    .or().like(SysUser::getPhone, keyword);
        }
        if (status != null) {
            userWrapper.eq(SysUser::getStatus, status);
        }
        userWrapper.orderByDesc(SysUser::getCreateTime);
        Page<SysUser> result = sysUserMapper.selectPage(userPage, userWrapper);

        // 组装VO
        List<UserProfileVO> voList = result.getRecords().stream().map(user -> {
            UserProfileVO vo = new UserProfileVO();
            vo.setUserId(user.getId());
            vo.setUsername(user.getUsername());
            vo.setRoleType(user.getRoleType());
            vo.setStatus(user.getStatus());
            vo.setPhone(maskPhone(user.getPhone()));
            vo.setEmail(maskEmail(user.getEmail()));

            UserProfile profile = userProfileMapper.selectOne(
                    new LambdaQueryWrapper<UserProfile>().eq(UserProfile::getUserId, user.getId())
            );
            if (profile != null) {
                vo.setRealName(profile.getRealName());
                vo.setGender(profile.getGender());
                vo.setUniversity(profile.getUniversity());
                vo.setMajor(profile.getMajor());
                vo.setGrade(profile.getGrade());
                vo.setBalance(profile.getBalance());
                vo.setCreditScore(profile.getCreditScore());
                vo.setAvatarUrl(profile.getAvatarUrl());
                vo.setVerifiedStatus(profile.getVerifiedStatus());
            }
            return vo;
        }).collect(Collectors.toList());

        return new PageResult<>(voList, result.getTotal(), result.getSize(), result.getCurrent());
    }

    @Override
    public void updateUserStatus(Long userId, Integer status) {
        SysUser sysUser = sysUserMapper.selectById(userId);
        if (sysUser == null) {
            throw new BusinessException("用户不存在");
        }
        sysUser.setStatus(status);
        sysUserMapper.updateById(sysUser);
    }

    /**
     * 手机号脱敏
     */
    private String maskPhone(String phone) {
        if (StrUtil.isBlank(phone) || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    /**
     * 邮箱脱敏
     */
    private String maskEmail(String email) {
        if (StrUtil.isBlank(email) || !email.contains("@")) {
            return email;
        }
        int atIndex = email.indexOf("@");
        if (atIndex <= 2) {
            return email;
        }
        return email.substring(0, 2) + "***" + email.substring(atIndex);
    }
}
