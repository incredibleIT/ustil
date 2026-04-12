package com.syit.cpc.service;

import com.syit.cpc.dto.request.LoginRequest;
import com.syit.cpc.dto.request.RegisterRequest;
import com.syit.cpc.dto.response.LoginResponse;

/**
 * 认证服务接口
 */
public interface AuthService {
    
    /**
     * 用户注册
     * @param request 注册请求
     * @return 注册成功消息
     */
    String register(RegisterRequest request);
    
    /**
     * 用户登录
     * @param request 登录请求
     * @return 登录响应（包含 Token 和用户信息）
     */
    LoginResponse login(LoginRequest request);
    
    /**
     * 刷新 Access Token
     * @param refreshToken Refresh Token
     * @return 新的 Token 对
     */
    LoginResponse refreshToken(String refreshToken);
}
