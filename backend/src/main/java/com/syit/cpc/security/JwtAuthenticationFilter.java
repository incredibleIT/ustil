package com.syit.cpc.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT 认证过滤器
 * 每个请求执行一次，验证 JWT Token 并设置 SecurityContext
 * 
 * 注意：此过滤器信任 Token 中的用户信息，不查询数据库
 * 如果需要验证用户状态，建议使用 Redis 缓存或 Token 黑名单机制
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtTokenProvider jwtTokenProvider;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 1. 从请求头提取 JWT Token
            String token = getTokenFromRequest(request);
            
            // 2. 验证 Token 并设置 SecurityContext
            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
                // 3. 从 Token 中获取用户信息（信任 Token，不查询数据库）
                String userId = jwtTokenProvider.getUserIdFromToken(token);
                String username = jwtTokenProvider.getUsernameFromToken(token);
                List<String> roles = jwtTokenProvider.getRolesFromToken(token);
                
                // 4. 创建 Authorities
                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
                
                // 5. 创建 Authentication 对象（使用 userId 作为 principal）
                UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(
                                userId,
                                null,
                                authorities
                        );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // 6. 设置 SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                log.debug("Set authentication for user: userId={}, username={}", userId, username);
            }
        } catch (Exception e) {
            // 捕获所有异常，避免影响请求处理
            log.error("Could not set user authentication in security context", e);
        }
        
        // 7. 继续过滤器链
        filterChain.doFilter(request, response);
    }
    
    /**
     * 从请求头中提取 JWT Token
     * 
     * @param request HTTP 请求
     * @return JWT Token 或 null
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
