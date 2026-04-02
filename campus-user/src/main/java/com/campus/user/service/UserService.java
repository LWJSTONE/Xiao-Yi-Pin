package com.campus.user.service;

import com.campus.common.dto.UserProfileDTO;
import com.campus.common.dto.VerifyApplyDTO;
import com.campus.common.result.PageResult;
import com.campus.common.vo.UserProfileVO;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 获取当前用户资料
     */
    UserProfileVO getMyProfile(Long userId);

    /**
     * 更新当前用户资料
     */
    UserProfileVO updateMyProfile(Long userId, UserProfileDTO dto);

    /**
     * 申请实名认证
     */
    void applyVerify(Long userId, VerifyApplyDTO dto);

    /**
     * 获取实名认证状态
     */
    Integer getVerifyStatus(Long userId);

    /**
     * 分页查询用户列表（管理员）
     */
    PageResult<UserProfileVO> listUsers(String keyword, Integer status, int page, int size);

    /**
     * 更新用户状态（管理员）
     */
    void updateUserStatus(Long userId, Integer status);
}
