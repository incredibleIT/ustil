package com.syit.cpc.controller;

import com.syit.cpc.common.response.ApiResponse;
import com.syit.cpc.dto.request.LoginRequest;
import com.syit.cpc.dto.request.RefreshTokenRequest;
import com.syit.cpc.dto.request.RegisterRequest;
import com.syit.cpc.dto.response.LoginResponse;
import com.syit.cpc.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户注册、登录等认证相关接口")
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "新用户注册账号")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "注册成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "参数错误或邮箱/学号已存在")
    })
    public ApiResponse<String> register(@Valid @RequestBody RegisterRequest request) {
        log.info("接收到用户注册请求: email={}", request.getEmail());
        String message = authService.register(request);
        return ApiResponse.success(message);
    }
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户通过邮箱和密码登录系统")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "登录成功，返回 Token"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "参数错误或登录失败"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "账号已锁定或未启用")
    })
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("接收到用户登录请求: email={}", request.getEmail());
        LoginResponse response = authService.login(request);
        return ApiResponse.success(response);
    }
    
    /**
     * 刷新 Token
     */
    @PostMapping("/refresh")
    @Operation(summary = "刷新 Token", description = "使用 Refresh Token 获取新的 Access Token")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "刷新成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Refresh Token 无效或已过期")
    })
    public ApiResponse<LoginResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("接收到刷新 Token 请求");
        LoginResponse response = authService.refreshToken(request.getRefreshToken());
        return ApiResponse.success(response);
    }
}
