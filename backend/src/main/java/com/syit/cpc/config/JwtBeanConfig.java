package com.syit.cpc.config;

import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * JWT Bean 配置
 * 创建 JWT 相关的 Spring Bean
 */
@Configuration
public class JwtBeanConfig {
    
    /**
     * 创建 SecretKey Bean
     * 从 JwtConfig 读取密钥并转换为 SecretKey 对象
     * 该 Bean 会被 JwtTokenProvider 注入，避免每次操作都重新创建
     */
    @Bean
    public SecretKey jwtSecretKey(JwtConfig jwtConfig) {
        return Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
    }
}
