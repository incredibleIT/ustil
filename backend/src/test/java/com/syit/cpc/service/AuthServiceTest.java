package com.syit.cpc.service;

import com.syit.cpc.common.exception.BusinessException;
import com.syit.cpc.common.exception.ErrorCode;
import com.syit.cpc.config.JwtConfig;
import com.syit.cpc.dto.request.LoginRequest;
import com.syit.cpc.dto.request.RegisterRequest;
import com.syit.cpc.dto.response.LoginResponse;
import com.syit.cpc.entity.User;
import com.syit.cpc.mapper.UserMapper;
import com.syit.cpc.security.JwtTokenProvider;
import com.syit.cpc.service.impl.AuthServiceImpl;
import com.syit.cpc.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AuthService 单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 单元测试")
class AuthServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordUtil passwordUtil;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private JwtConfig jwtConfig;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest validRegisterRequest;
    private LoginRequest validLoginRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        validRegisterRequest = new RegisterRequest();
        validRegisterRequest.setStudentId("2021001234");
        validRegisterRequest.setUsername("张三");
        validRegisterRequest.setEmail("test@example.com");
        validRegisterRequest.setPassword("password123");
        validRegisterRequest.setConfirmPassword("password123");

        validLoginRequest = new LoginRequest();
        validLoginRequest.setEmail("test@example.com");
        validLoginRequest.setPassword("password123");
        validLoginRequest.setRememberMe(false);

        testUser = new User();
        testUser.setId(1L);
        testUser.setStudentId("2021001234");
        testUser.setUsername("张三");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole("ROLE_PROBATION");
        testUser.setStatus(1);
        testUser.setLoginFailCount(0);
        testUser.setLockUntil(null);
    }

    @Nested
    @DisplayName("用户注册测试")
    class RegisterTests {

        @Test
        @DisplayName("注册成功 - 正常流程")
        void register_Success() {
            // Given
            when(userMapper.selectCount(any())).thenReturn(0L);
            when(passwordUtil.encodePassword("password123")).thenReturn("encodedPassword");
            when(userMapper.insert(any(User.class))).thenReturn(1);

            // When
            String result = authService.register(validRegisterRequest);

            // Then
            assertEquals("注册成功，请登录", result);
            verify(userMapper, times(2)).selectCount(any()); // 验证邮箱和学号
            verify(passwordUtil, times(1)).encodePassword("password123");
            verify(userMapper, times(1)).insert(any(User.class));
        }

        @Test
        @DisplayName("注册失败 - 邮箱已存在")
        void register_Fail_EmailExists() {
            // Given
            when(userMapper.selectCount(any())).thenReturn(1L);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> authService.register(validRegisterRequest));
            assertEquals(ErrorCode.EMAIL_ALREADY_EXISTS, exception.getErrorCode());
            verify(userMapper, times(1)).selectCount(any());
            verify(userMapper, never()).insert(any(User.class));
        }

        @Test
        @DisplayName("注册失败 - 学号已存在")
        void register_Fail_StudentIdExists() {
            // Given - 邮箱不存在，但学号存在
            when(userMapper.selectCount(any()))
                    .thenReturn(0L) // 第一次调用：邮箱检查
                    .thenReturn(1L); // 第二次调用：学号检查

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> authService.register(validRegisterRequest));
            assertEquals(ErrorCode.STUDENT_NO_ALREADY_EXISTS, exception.getErrorCode());
            verify(userMapper, times(2)).selectCount(any());
            verify(userMapper, never()).insert(any(User.class));
        }

        @Test
        @DisplayName("注册失败 - 数据库插入失败")
        void register_Fail_DatabaseInsertFailed() {
            // Given
            when(userMapper.selectCount(any())).thenReturn(0L);
            when(passwordUtil.encodePassword(anyString())).thenReturn("encodedPassword");
            when(userMapper.insert(any(User.class))).thenReturn(0);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> authService.register(validRegisterRequest));
            assertEquals(ErrorCode.INTERNAL_ERROR, exception.getErrorCode());
        }

        @Test
        @DisplayName("注册成功 - 验证用户初始角色为预备成员")
        void register_Success_VerifyInitialRole() {
            // Given
            when(userMapper.selectCount(any())).thenReturn(0L);
            when(passwordUtil.encodePassword(anyString())).thenReturn("encodedPassword");
            when(userMapper.insert(any(User.class))).thenReturn(1);

            // When
            authService.register(validRegisterRequest);

            // Then - 验证插入的用户对象
            verify(userMapper).insert(argThat((User user) ->
                    "ROLE_PROBATION".equals(user.getRole()) &&
                    user.getStatus() == 1
            ));
        }
    }

    @Nested
    @DisplayName("用户登录测试")
    class LoginTests {

        @Test
        @DisplayName("登录成功 - 正常流程")
        void login_Success() {
            // Given
            when(userMapper.selectOne(any())).thenReturn(testUser);
            when(passwordUtil.matchesPassword("password123", "encodedPassword")).thenReturn(true);
            when(jwtTokenProvider.generateAccessToken(anyString(), anyString(), anyList()))
                    .thenReturn("accessToken");
            when(jwtTokenProvider.generateRefreshToken(anyString()))
                    .thenReturn("refreshToken");
            when(jwtConfig.getAccessTokenExpiration()).thenReturn(86400000L);

            // When
            LoginResponse response = authService.login(validLoginRequest);

            // Then
            assertNotNull(response);
            assertEquals("accessToken", response.getAccessToken());
            assertEquals("refreshToken", response.getRefreshToken());
            assertEquals("Bearer", response.getTokenType());
            assertEquals(86400L, response.getExpiresIn());
            assertEquals(1L, response.getUserId());
            assertEquals("张三", response.getUsername());
            assertEquals(Collections.singletonList("ROLE_PROBATION"), response.getRoles());

            verify(userMapper).selectOne(any());
            verify(passwordUtil).matchesPassword("password123", "encodedPassword");
            verify(jwtTokenProvider).generateAccessToken("1", "张三", Collections.singletonList("ROLE_PROBATION"));
            verify(jwtTokenProvider).generateRefreshToken("1");
        }

        @Test
        @DisplayName("登录失败 - 用户不存在")
        void login_Fail_UserNotFound() {
            // Given
            when(userMapper.selectOne(any())).thenReturn(null);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> authService.login(validLoginRequest));
            assertEquals(ErrorCode.LOGIN_FAILED, exception.getErrorCode());
            verify(userMapper).selectOne(any());
            verify(passwordUtil, never()).matchesPassword(anyString(), anyString());
        }

        @Test
        @DisplayName("登录失败 - 账号未启用")
        void login_Fail_AccountNotActivated() {
            // Given
            testUser.setStatus(0);
            when(userMapper.selectOne(any())).thenReturn(testUser);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> authService.login(validLoginRequest));
            assertEquals(ErrorCode.ACCOUNT_NOT_ACTIVATED, exception.getErrorCode());
        }

        @Test
        @DisplayName("登录失败 - 账号已锁定")
        void login_Fail_AccountLocked() {
            // Given
            testUser.setLockUntil(LocalDateTime.now().plusMinutes(10));
            when(userMapper.selectOne(any())).thenReturn(testUser);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> authService.login(validLoginRequest));
            assertEquals(ErrorCode.ACCOUNT_LOCKED, exception.getErrorCode());
        }

        @Test
        @DisplayName("登录失败 - 密码错误")
        void login_Fail_WrongPassword() {
            // Given
            when(userMapper.selectOne(any())).thenReturn(testUser);
            when(passwordUtil.matchesPassword("password123", "encodedPassword")).thenReturn(false);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> authService.login(validLoginRequest));
            assertEquals(ErrorCode.LOGIN_FAILED, exception.getErrorCode());
            verify(userMapper, atLeastOnce()).update(any(), any()); // 验证调用原子更新
            verify(userMapper, atLeastOnce()).selectById(anyLong()); // 验证重新查询用户
            verify(userMapper, never()).updateById(any(User.class)); // 不直接调用 updateById
        }

        @Test
        @DisplayName("登录成功 - 锁定时间已过允许登录")
        void login_Success_LockExpired() {
            // Given
            testUser.setLockUntil(LocalDateTime.now().minusMinutes(20)); // 锁定已过
            when(userMapper.selectOne(any())).thenReturn(testUser);
            when(passwordUtil.matchesPassword("password123", "encodedPassword")).thenReturn(true);
            when(jwtTokenProvider.generateAccessToken(anyString(), anyString(), anyList()))
                    .thenReturn("accessToken");
            when(jwtTokenProvider.generateRefreshToken(anyString())).thenReturn("refreshToken");
            when(jwtConfig.getAccessTokenExpiration()).thenReturn(86400000L);

            // When
            LoginResponse response = authService.login(validLoginRequest);

            // Then
            assertNotNull(response);
            assertEquals("accessToken", response.getAccessToken());
        }
    }

    @Nested
    @DisplayName("登录锁定机制测试")
    class LoginLockTests {

        @Test
        @DisplayName("连续5次失败后账号锁定")
        void login_LockAfter5Failures() {
            // Given
            testUser.setLoginFailCount(4); // 已经失败4次
            when(userMapper.selectOne(any())).thenReturn(testUser);
            when(passwordUtil.matchesPassword(anyString(), anyString())).thenReturn(false);

            // When & Then
            assertThrows(BusinessException.class,
                    () -> authService.login(validLoginRequest));

            // 验证调用了 update 方法（原子更新 + 设置锁定时间）
            verify(userMapper, atLeastOnce()).update(any(), any());
            verify(userMapper, atLeastOnce()).selectById(anyLong());
        }

        @Test
        @DisplayName("登录成功后重置失败次数")
        void login_ResetFailCountOnSuccess() {
            // Given - 创建新对象避免数据污染
            User user = new User();
            user.setId(1L);
            user.setStudentId("2021001234");
            user.setUsername("张三");
            user.setEmail("test@example.com");
            user.setPassword("encodedPassword");
            user.setRole("ROLE_PROBATION");
            user.setStatus(1);
            user.setLoginFailCount(3);
            user.setLockUntil(LocalDateTime.now().minusMinutes(10)); // 锁定已过期
            when(userMapper.selectOne(any())).thenReturn(user);
            when(passwordUtil.matchesPassword("password123", "encodedPassword")).thenReturn(true);
            when(jwtTokenProvider.generateAccessToken(anyString(), anyString(), anyList()))
                    .thenReturn("accessToken");
            when(jwtTokenProvider.generateRefreshToken(anyString())).thenReturn("refreshToken");
            when(jwtConfig.getAccessTokenExpiration()).thenReturn(86400000L);

            // When
            authService.login(validLoginRequest);

            // Then
            verify(userMapper).updateById(argThat((User u) ->
                    u.getLoginFailCount() == 0 &&
                    u.getLockUntil() == null
            ));
        }
    }

    @Nested
    @DisplayName("Token 刷新测试")
    class RefreshTokenTests {

        @Test
        @DisplayName("刷新 Token 成功")
        void refreshToken_Success() {
            // Given
            String validRefreshToken = "validRefreshToken";
            when(jwtTokenProvider.validateToken(validRefreshToken)).thenReturn(true);
            when(jwtTokenProvider.getUserIdFromToken(validRefreshToken)).thenReturn("1");
            when(userMapper.selectById(1L)).thenReturn(testUser);
            when(jwtTokenProvider.generateAccessToken(anyString(), anyString(), anyList()))
                    .thenReturn("newAccessToken");
            when(jwtTokenProvider.generateRefreshToken(anyString())).thenReturn("newRefreshToken");
            when(jwtConfig.getAccessTokenExpiration()).thenReturn(86400000L);

            // When
            LoginResponse response = authService.refreshToken(validRefreshToken);

            // Then
            assertNotNull(response);
            assertEquals("newAccessToken", response.getAccessToken());
            assertEquals("newRefreshToken", response.getRefreshToken());
            assertEquals(1L, response.getUserId());
            assertEquals("张三", response.getUsername());
        }

        @Test
        @DisplayName("刷新 Token 失败 - Token 无效")
        void refreshToken_Fail_InvalidToken() {
            // Given
            String invalidRefreshToken = "invalidRefreshToken";
            when(jwtTokenProvider.validateToken(invalidRefreshToken)).thenReturn(false);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> authService.refreshToken(invalidRefreshToken));
            assertEquals(ErrorCode.REFRESH_TOKEN_INVALID, exception.getErrorCode());
            verify(userMapper, never()).selectById(anyLong());
        }

        @Test
        @DisplayName("刷新 Token 失败 - 用户不存在")
        void refreshToken_Fail_UserNotFound() {
            // Given
            String validRefreshToken = "validRefreshToken";
            when(jwtTokenProvider.validateToken(validRefreshToken)).thenReturn(true);
            when(jwtTokenProvider.getUserIdFromToken(validRefreshToken)).thenReturn("999");
            when(userMapper.selectById(999L)).thenReturn(null);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> authService.refreshToken(validRefreshToken));
            assertEquals(ErrorCode.REFRESH_TOKEN_INVALID, exception.getErrorCode());
        }

        @Test
        @DisplayName("刷新 Token 失败 - 用户未启用")
        void refreshToken_Fail_UserNotActivated() {
            // Given
            String validRefreshToken = "validRefreshToken";
            testUser.setStatus(0);
            when(jwtTokenProvider.validateToken(validRefreshToken)).thenReturn(true);
            when(jwtTokenProvider.getUserIdFromToken(validRefreshToken)).thenReturn("1");
            when(userMapper.selectById(1L)).thenReturn(testUser);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> authService.refreshToken(validRefreshToken));
            assertEquals(ErrorCode.REFRESH_TOKEN_INVALID, exception.getErrorCode());
        }

        @Test
        @DisplayName("刷新 Token 失败 - userId 格式错误")
        void refreshToken_Fail_InvalidUserIdFormat() {
            // Given
            String validRefreshToken = "validRefreshToken";
            when(jwtTokenProvider.validateToken(validRefreshToken)).thenReturn(true);
            when(jwtTokenProvider.getUserIdFromToken(validRefreshToken)).thenReturn("invalidId");

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> authService.refreshToken(validRefreshToken));
            assertEquals(ErrorCode.REFRESH_TOKEN_INVALID, exception.getErrorCode());
        }
    }
}
