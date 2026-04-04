package com.campus.user.feign;

import com.campus.common.vo.UserProfileVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * 用户Feign客户端
 * <p>
 * 预留接口，用于未来跨服务调用（如 order/gateway 模块需要获取用户信息时）。
 * 当前 campus-user 模块内部不使用此客户端，调用自身服务请直接注入 Service 或 Mapper。
 * 启用前需配合 fallbackFactory 实现熔断降级逻辑。
 * </p>
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
