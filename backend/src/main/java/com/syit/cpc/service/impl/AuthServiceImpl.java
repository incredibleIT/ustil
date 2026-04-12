package com.syit.cpc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.syit.cpc.common.exception.BusinessException;
import com.syit.cpc.common.exception.ErrorCode;
import com.syit.cpc.config.JwtConfig;
import com.syit.cpc.dto.request.LoginRequest;
import com.syit.cpc.dto.request.RegisterRequest;
import com.syit.cpc.dto.response.LoginResponse;
import com.syit.cpc.entity.User;
import com.syit.cpc.mapper.UserMapper;
import com.syit.cpc.security.JwtTokenProvider;
import com.syit.cpc.service.AuthService;
import com.syit.cpc.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 认证服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    private final UserMapper userMapper;
    private final PasswordUtil passwordUtil;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtConfig jwtConfig;
    
    /**
     * 用户注册
     * @param request 注册请求
     * @return 注册成功消息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String register(RegisterRequest request) {
        log.info("用户注册请求: email={}, studentId={}", request.getEmail(), request.getStudentId());
        
        // 1. 验证邮箱是否已存在
        LambdaQueryWrapper<User> emailWrapper = new LambdaQueryWrapper<>();
        emailWrapper.eq(User::getEmail, request.getEmail());
        if (userMapper.selectCount(emailWrapper) > 0) {
            log.warn("注册失败: 邮箱已存在 - {}", request.getEmail());
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        
        // 2. 验证学号是否已存在
        LambdaQueryWrapper<User> studentIdWrapper = new LambdaQueryWrapper<>();
        studentIdWrapper.eq(User::getStudentId, request.getStudentId());
        if (userMapper.selectCount(studentIdWrapper) > 0) {
            log.warn("注册失败: 学号已存在 - {}", request.getStudentId());
            throw new BusinessException(ErrorCode.STUDENT_NO_ALREADY_EXISTS);
        }
        
        // 注意：密码一致性验证已由 DTO 层的 @PasswordMatch 注解处理
        
        // 3. 创建用户实体
        User user = new User();
        user.setStudentId(request.getStudentId());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordUtil.encodePassword(request.getPassword()));
        user.setRole("ROLE_PROBATION");  // 初始角色：预备成员
        user.setStatus(1);  // 状态：1-启用（简化流程，注册即可用）
        
        // 4. 保存到数据库
        int result = userMapper.insert(user);
        if (result <= 0) {
            log.error("用户注册失败: 数据库插入失败");
            throw new BusinessException(ErrorCode.INTERNAL_ERROR);
        }
        
        log.info("用户注册成功: id={}, email={}", user.getId(), user.getEmail());
        return "注册成功，请登录";
    }
    
    /**
     * 用户登录
     * @param request 登录请求
     * @return 登录响应（包含 Token 和用户信息）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginResponse login(LoginRequest request) {
        log.info("用户登录请求: email={}", request.getEmail());
        
        // 1. 根据邮箱查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, request.getEmail());
        User user = userMapper.selectOne(wrapper);
        
        if (user == null) {
            log.warn("登录失败: 邮箱不存在 - {}", request.getEmail());
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }
        
        // 2. 验证用户状态
        if (user.getStatus() != 1) {
            log.warn("登录失败: 账号未启用 - email={}, status={}", request.getEmail(), user.getStatus());
            throw new BusinessException(ErrorCode.ACCOUNT_NOT_ACTIVATED);
        }
        
        // 3. 检查账号是否被锁定
        if (user.getLockUntil() != null && user.getLockUntil().isAfter(LocalDateTime.now())) {
            log.warn("登录失败: 账号已锁定 - email={}, lockUntil={}", request.getEmail(), user.getLockUntil());
            throw new BusinessException(ErrorCode.ACCOUNT_LOCKED);
        }
        
        // 4. 验证密码
        if (!passwordUtil.matchesPassword(request.getPassword(), user.getPassword())) {
            // 密码错误，增加失败次数
            log.warn("登录失败: 密码错误 - email={}", request.getEmail());
            incrementLoginFailCount(user);
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }
        
        // 5. 登录成功，重置失败次数
        log.info("登录成功: id={}, email={}", user.getId(), user.getEmail());
        resetLoginFailCount(user);
        
        // 6. 生成 Token
        List<String> roles = Collections.singletonList(user.getRole());
        String accessToken = jwtTokenProvider.generateAccessToken(
            user.getId().toString(), user.getUsername(), roles
        );
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId().toString());
        
        // 7. 构建响应
        return LoginResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .expiresIn(jwtConfig.getAccessTokenExpiration() / 1000)
            .userId(user.getId())
            .username(user.getUsername())
            .roles(roles)
            .build();
    }
    
    /**
     * 刷新 Access Token
     * @param refreshToken Refresh Token
     * @return 新的 Token 对
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginResponse refreshToken(String refreshToken) {
        log.info("刷新 Token 请求");
        
        // 1. 验证 Refresh Token
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            log.warn("刷新 Token 失败: Token 无效或已过期");
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_INVALID);
        }
        
        // 2. 从 Token 中获取用户 ID
        String userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        
        // 3. 查询用户是否存在且状态正常
        try {
            Long userIdLong = Long.parseLong(userId);
            User user = userMapper.selectById(userIdLong);
            if (user == null || user.getStatus() != 1) {
                log.warn("刷新 Token 失败: 用户不存在或未启用 - userId={}", userId);
                throw new BusinessException(ErrorCode.REFRESH_TOKEN_INVALID);
            }
            
            // 4. 生成新的 Token
            List<String> roles = Collections.singletonList(user.getRole());
            String newAccessToken = jwtTokenProvider.generateAccessToken(
                user.getId().toString(), user.getUsername(), roles
            );
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getId().toString());
            
            log.info("刷新 Token 成功: userId={}", user.getId());
            
            // 5. 构建响应
            return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtConfig.getAccessTokenExpiration() / 1000)
                .userId(user.getId())
                .username(user.getUsername())
                .roles(roles)
                .build();
        } catch (NumberFormatException e) {
            log.error("Refresh Token 包含无效的 userId: {}", userId);
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_INVALID);
        }
    }
    
    /**
     * 增加登录失败次数
     * 使用数据库原子操作避免并发竞态条件
     * @param user 用户实体
     */
    private void incrementLoginFailCount(User user) {
        // 使用数据库原子操作：login_fail_count = login_fail_count + 1
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getId, user.getId())
                     .setSql("login_fail_count = COALESCE(login_fail_count, 0) + 1");
        userMapper.update(null, updateWrapper);
        
        // 重新查询用户，检查是否需要锁定
        User updatedUser = userMapper.selectById(user.getId());
        if (updatedUser != null && updatedUser.getLoginFailCount() != null && updatedUser.getLoginFailCount() >= 5) {
            // 如果当前未锁定或锁定时间已过期，则设置新的锁定时间
            if (updatedUser.getLockUntil() == null || updatedUser.getLockUntil().isBefore(LocalDateTime.now())) {
                updatedUser.setLockUntil(LocalDateTime.now().plusMinutes(15));
                userMapper.updateById(updatedUser);
                log.warn("账号已锁定: userId={}, lockUntil={}", updatedUser.getId(), updatedUser.getLockUntil());
            }
        }
    }
    
    /**
     * 重置登录失败次数
     * @param user 用户实体
     */
    private void resetLoginFailCount(User user) {
        user.setLoginFailCount(0);
        user.setLockUntil(null);
        userMapper.updateById(user);
    }
}
