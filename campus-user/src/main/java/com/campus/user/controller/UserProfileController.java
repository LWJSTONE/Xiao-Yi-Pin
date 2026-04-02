package com.campus.user.controller;

import com.campus.common.dto.UserProfileDTO;
import com.campus.common.dto.VerifyApplyDTO;
import com.campus.common.exception.BusinessException;
import com.campus.common.result.PageResult;
import com.campus.common.result.R;
import com.campus.common.vo.UserProfileVO;
import com.campus.user.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户资料控制器
 */
@RestController
@RequestMapping("/api/v1/user")
public class UserProfileController {

    private final UserService userService;

    public UserProfileController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 获取当前用户资料
     */
    @GetMapping("/profile/me")
    public R<UserProfileVO> getMyProfile(@RequestHeader("X-User-Id") Long userId) {
        return R.ok(userService.getMyProfile(userId));
    }

    /**
     * 更新当前用户资料
     */
    @PutMapping("/profile/me")
    public R<UserProfileVO> updateMyProfile(
            @RequestHeader("X-User-Id") Long userId,
            @Validated @RequestBody UserProfileDTO dto) {
        return R.ok(userService.updateMyProfile(userId, dto));
    }

    /**
     * 申请实名认证
     */
    @PostMapping("/verify/apply")
    public R<Void> applyVerify(
            @RequestHeader("X-User-Id") Long userId,
            @Validated @RequestBody VerifyApplyDTO dto) {
        userService.applyVerify(userId, dto);
        return R.ok();
    }

    /**
     * 获取实名认证状态
     */
    @GetMapping("/verify/status")
    public R<Integer> getVerifyStatus(@RequestHeader("X-User-Id") Long userId) {
        return R.ok(userService.getVerifyStatus(userId));
    }

    /**
     * 分页查询用户列表（管理员）
     */
    @GetMapping("/admin/users")
    public R<PageResult<UserProfileVO>> listUsers(
            @RequestHeader("X-Role-Type") String roleType,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String roleTypeFilter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (!"ADMIN".equals(roleType)) {
            throw new BusinessException(403, "无权限访问");
        }
        return R.ok(userService.listUsers(keyword, status, roleTypeFilter, page, size));
    }

    /**
     * 更新用户状态（管理员）
     */
    @PutMapping("/admin/users/{userId}/status")
    public R<Void> updateUserStatus(
            @RequestHeader("X-Role-Type") String roleType,
            @PathVariable Long userId,
            @RequestParam Integer status) {
        if (!"ADMIN".equals(roleType)) {
            throw new BusinessException(403, "无权限访问");
        }
        userService.updateUserStatus(userId, status);
        return R.ok();
    }
}
