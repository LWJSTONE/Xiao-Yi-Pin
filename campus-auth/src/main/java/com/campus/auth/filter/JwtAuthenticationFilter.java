package com.campus.auth.filter;

import com.campus.common.constant.CommonConstant;
import com.campus.common.constant.RedisConstant;
import com.campus.common.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * JWT认证过滤器
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 获取请求头中的Token
        String authHeader = request.getHeader(CommonConstant.TOKEN_HEADER);
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith(CommonConstant.TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 提取Token
        String token = authHeader.substring(CommonConstant.TOKEN_PREFIX.length());

        try {
            // 解析Token
            Claims claims = JwtUtils.parseToken(token, jwtSecret);
            String userId = claims.getSubject();
            String username = (String) claims.get("username");
            String roleType = (String) claims.get("roleType");

            // 检查Redis黑名单
            String blacklistKey = RedisConstant.USER_BLACKLIST + userId;
            try {
                Boolean isBlacklisted = stringRedisTemplate.hasKey(blacklistKey);
                if (Boolean.TRUE.equals(isBlacklisted)) {
                    log.warn("用户已登出，userId={}", userId);
                    filterChain.doFilter(request, response);
                    return;
                }
            } catch (Exception e) {
                log.error("检查Redis黑名单失败，放行请求", e);
                // Redis不可用时放行请求，由下游服务自行处理
            }

            // 构建权限列表
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_" + roleType));

            // 创建认证Token并设置到Security上下文
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userId, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            log.debug("JWT认证成功, userId={}, username={}, roleType={}", userId, username, roleType);
        } catch (Exception e) {
            log.warn("JWT Token解析失败: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
