package com.syit.cpc.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * JWT 配置类
 * 从 application.yml 读取 JWT 相关配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    
    /**
     * JWT 签名密钥（至少 256 位 = 32 字节）
     */
    private String secret;
    
    /**
     * Access Token 有效期（毫秒）
     * 默认 24 小时：86400000
     */
    private Long accessTokenExpiration;
    
    /**
     * Refresh Token 有效期（毫秒）
     * 默认 7 天：604800000
     */
    private Long refreshTokenExpiration;
    
    /**
     * 验证 JWT 配置
     */
    @PostConstruct
    public void validate() {
        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException(
                "JWT secret must be at least 32 characters (256 bits) for HS256 algorithm. " +
                "Current length: " + (secret == null ? 0 : secret.length())
            );
        }
    }
}
