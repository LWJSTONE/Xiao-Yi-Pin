package com.campus.gateway.filter;

import com.alibaba.fastjson2.JSON;
import com.campus.common.constant.CommonConstant;
import com.campus.common.constant.RedisConstant;
import com.campus.common.result.R;
import com.campus.common.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * 全局认证过滤器
 */
@Slf4j
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Value("${jwt.secret}")
    private String jwtSecret;

    /** 白名单路径 */
    private static final List<String> WHITE_LIST = Arrays.asList(
            "/api/v1/auth/login",
            "/api/v1/auth/refresh",
            "/api/v1/auth/register",
            "/api/v1/auth/captcha"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String path = request.getURI().getPath();
        log.debug("Gateway request path: {}", path);

        // 白名单放行
        for (String whitePath : WHITE_LIST) {
            if (path.equals(whitePath) || path.startsWith(whitePath)) {
                return chain.filter(exchange);
            }
        }

        // 获取Authorization头
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(CommonConstant.TOKEN_PREFIX)) {
            return unauthorizedResponse(response, "未登录或Token格式错误");
        }

        // 提取Token
        String token = authHeader.substring(CommonConstant.TOKEN_PREFIX.length());
        Claims claims;
        try {
            claims = JwtUtils.parseToken(token, jwtSecret);
        } catch (Exception e) {
            log.warn("Token解析失败: {}", e.getMessage());
            return unauthorizedResponse(response, "Token无效或已过期");
        }

        // 提取用户信息
        String userId = claims.getSubject();
        String username = (String) claims.get("username");
        String roleType = (String) claims.get("roleType");

        // 检查Redis黑名单
        String blacklistKey = RedisConstant.USER_BLACKLIST + userId;
        Boolean isBlacklisted = stringRedisTemplate.hasKey(blacklistKey);
        if (Boolean.TRUE.equals(isBlacklisted)) {
            return unauthorizedResponse(response, "用户已登出，请重新登录");
        }

        // 将用户信息添加到请求头，传递给下游服务
        ServerHttpRequest mutatedRequest = request.mutate()
                .header("X-User-Id", userId)
                .header("X-Username", username)
                .header("X-Role-Type", roleType)
                .build();

        ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();

        log.debug("Gateway auth passed, userId={}, username={}, roleType={}", userId, username, roleType);
        return chain.filter(mutatedExchange);
    }

    @Override
    public int getOrder() {
        return -100;
    }

    /**
     * 返回401未授权响应
     */
    private Mono<Void> unauthorizedResponse(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        R<Void> result = R.error(401, message);
        String body = JSON.toJSONString(result);
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}
