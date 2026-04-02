package com.campus.user.feign;

import com.campus.common.vo.UserProfileVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * 用户Feign客户端
 */
@FeignClient(name = "campus-user", contextId = "userFeignClient")
public interface UserFeignClient {

    /**
     * 获取用户资料（通过X-User-Id请求头获取指定用户的资料）
     * 注意：调用方需在请求头中设置X-User-Id为目标用户ID
     */
    @GetMapping("/api/v1/user/profile/me")
    UserProfileVO getUserProfile(@RequestHeader("X-User-Id") Long userId);
}
