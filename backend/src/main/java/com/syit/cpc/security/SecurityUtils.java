package com.syit.cpc.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * 安全上下文工具类
 * 用于获取当前登录用户信息
 */
@Slf4j
@Component
public class SecurityUtils {

    /**
     * 获取当前登录用户 ID
     * 
     * @return 用户 ID
     */
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new IllegalStateException("未认证用户");
        }

        // 从 Authentication 的 principal 中获取 userId（已在 JwtAuthenticationFilter 中设置）
        String userIdStr = authentication.getName();
        try {
            return Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            log.error("Invalid user ID in SecurityContext: {}", userIdStr);
            throw new IllegalStateException("无效的用户 ID");
        }
    }

    /**
     * 检查当前用户是否为管理员
     * 从 JWT Token 中的 authorities 直接获取角色，无需查询数据库
     * 
     * @return true-是管理员, false-不是管理员
     */
    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        return authorities.stream()
                .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));
    }
}
