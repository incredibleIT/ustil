package com.syit.cpc.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 用户登录响应 DTO
 */
@Data
@Builder
@Schema(description = "用户登录响应")
public class LoginResponse {
    
    @Schema(description = "Access Token")
    private String accessToken;
    
    @Schema(description = "Refresh Token")
    private String refreshToken;
    
    @Schema(description = "Token 类型", example = "Bearer")
    private String tokenType;
    
    @Schema(description = "Access Token 有效期（秒）", example = "86400")
    private Long expiresIn;
    
    @Schema(description = "用户 ID")
    private Long userId;
    
    @Schema(description = "用户名")
    private String username;
    
    @Schema(description = "角色列表")
    private List<String> roles;
}
