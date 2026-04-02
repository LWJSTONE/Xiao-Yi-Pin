package com.campus.user.feign;

import com.campus.common.entity.SysUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 用户Feign客户端
 */
@FeignClient(name = "campus-user")
public interface UserFeignClient {

    @GetMapping("/api/v1/user/profile/{userId}")
    SysUser getUserById(@PathVariable("userId") Long userId);
}
