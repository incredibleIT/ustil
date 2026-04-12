package com.syit.cpc.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.syit.cpc.common.exception.BusinessException;
import com.syit.cpc.common.exception.ErrorCode;
import com.syit.cpc.dto.request.ChangePasswordRequest;
import com.syit.cpc.dto.request.UpdateProfileRequest;
import com.syit.cpc.dto.request.UpdateUserRoleRequest;
import com.syit.cpc.dto.request.UpdateUserStatusRequest;
import com.syit.cpc.dto.response.MemberListResponse;
import com.syit.cpc.dto.response.UserProfileResponse;
import com.syit.cpc.entity.User;
import com.syit.cpc.mapper.UserMapper;
import com.syit.cpc.service.impl.UserServiceImpl;
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
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * UserService 单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 单元测试")
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordUtil passwordUtil;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setStudentId("2021001234");
        testUser.setUsername("张三");
        testUser.setEmail("zhangsan@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole("ROLE_MEMBER");
        testUser.setAvatar("https://example.com/avatar.jpg");
        testUser.setBio("这是一个测试用户");
        testUser.setStatus(1);
        testUser.setCreatedAt(LocalDateTime.of(2026, 4, 1, 10, 0, 0));
        testUser.setLoginFailCount(0);
        testUser.setLockUntil(null);
    }

    @Nested
    @DisplayName("获取用户信息测试")
    class GetUserProfileTests {

        @Test
        @DisplayName("获取用户信息成功")
        void getUserProfile_Success() {
            // Given
            when(userMapper.selectById(1L)).thenReturn(testUser);

            // When
            UserProfileResponse response = userService.getUserProfile(1L);

            // Then
            assertNotNull(response);
            assertEquals(1L, response.getUserId());
            assertEquals("2021001234", response.getStudentId());
            assertEquals("张三", response.getUsername());
            assertEquals("zhangsan@example.com", response.getEmail());
            assertEquals("ROLE_MEMBER", response.getRole());
            assertEquals("https://example.com/avatar.jpg", response.getAvatar());
            assertEquals("这是一个测试用户", response.getBio());
            assertEquals(1, response.getStatus());

            verify(userMapper).selectById(1L);
        }

        @Test
        @DisplayName("获取用户信息失败 - 用户不存在")
        void getUserProfile_Fail_UserNotFound() {
            // Given
            when(userMapper.selectById(999L)).thenReturn(null);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> userService.getUserProfile(999L));
            assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("修改个人信息测试")
    class UpdateProfileTests {

        @Test
        @DisplayName("修改个人信息成功 - 更新所有字段")
        void updateProfile_Success_AllFields() {
            // Given
            when(userMapper.selectById(1L)).thenReturn(testUser);
            when(userMapper.updateById(any(User.class))).thenReturn(1);

            UpdateProfileRequest request = new UpdateProfileRequest();
            request.setUsername("新昵称");
            request.setBio("新的个人简介");

            // When
            userService.updateProfile(1L, request);

            // Then
            verify(userMapper).updateById(any(User.class));
        }

        @Test
        @DisplayName("修改个人信息成功 - 只更新昵称")
        void updateProfile_Success_OnlyUsername() {
            // Given
            when(userMapper.selectById(1L)).thenReturn(testUser);
            when(userMapper.updateById(any(User.class))).thenReturn(1);

            UpdateProfileRequest request = new UpdateProfileRequest();
            request.setUsername("新昵称");
            request.setBio(null);

            // When
            userService.updateProfile(1L, request);

            // Then
            verify(userMapper).updateById(any(User.class));
        }

        @Test
        @DisplayName("修改个人信息成功 - 只更新简介")
        void updateProfile_Success_OnlyBio() {
            // Given
            when(userMapper.selectById(1L)).thenReturn(testUser);
            when(userMapper.updateById(any(User.class))).thenReturn(1);

            UpdateProfileRequest request = new UpdateProfileRequest();
            request.setUsername(null);
            request.setBio("新的个人简介");

            // When
            userService.updateProfile(1L, request);

            // Then
            verify(userMapper).updateById(any(User.class));
        }

        @Test
        @DisplayName("修改个人信息失败 - 用户不存在")
        void updateProfile_Fail_UserNotFound() {
            // Given
            when(userMapper.selectById(999L)).thenReturn(null);

            UpdateProfileRequest request = new UpdateProfileRequest();
            request.setUsername("新昵称");

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> userService.updateProfile(999L, request));
            assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
            verify(userMapper, never()).updateById(any(User.class));
        }

        @Test
        @DisplayName("修改个人信息 - 空请求不更新")
        void updateProfile_NoUpdate_EmptyRequest() {
            // Given
            when(userMapper.selectById(1L)).thenReturn(testUser);

            UpdateProfileRequest request = new UpdateProfileRequest();
            request.setUsername(null);
            request.setBio(null);

            // When
            userService.updateProfile(1L, request);

            // Then
            verify(userMapper, never()).updateById(any(User.class));
        }
    }

    @Nested
    @DisplayName("修改密码测试")
    class ChangePasswordTests {

        @Test
        @DisplayName("修改密码成功")
        void changePassword_Success() {
            // Given
            when(userMapper.selectById(1L)).thenReturn(testUser);
            when(passwordUtil.matchesPassword("oldPassword", "encodedPassword")).thenReturn(true);
            when(passwordUtil.encodePassword("newPassword123")).thenReturn("newEncodedPassword");
            when(userMapper.updateById(any(User.class))).thenReturn(1);

            ChangePasswordRequest request = new ChangePasswordRequest();
            request.setOldPassword("oldPassword");
            request.setNewPassword("newPassword123");
            request.setConfirmPassword("newPassword123");

            // When
            userService.changePassword(1L, request);

            // Then
            verify(userMapper).updateById(any(User.class));
        }

        @Test
        @DisplayName("修改密码失败 - 旧密码错误")
        void changePassword_Fail_WrongOldPassword() {
            // Given
            when(userMapper.selectById(1L)).thenReturn(testUser);
            when(passwordUtil.matchesPassword("wrongPassword", "encodedPassword")).thenReturn(false);

            ChangePasswordRequest request = new ChangePasswordRequest();
            request.setOldPassword("wrongPassword");
            request.setNewPassword("newPassword123");
            request.setConfirmPassword("newPassword123");

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> userService.changePassword(1L, request));
            assertEquals(ErrorCode.OLD_PASSWORD_ERROR, exception.getErrorCode());
            verify(userMapper, never()).updateById(any(User.class));
        }

        @Test
        @DisplayName("修改密码失败 - 用户不存在")
        void changePassword_Fail_UserNotFound() {
            // Given
            when(userMapper.selectById(999L)).thenReturn(null);

            ChangePasswordRequest request = new ChangePasswordRequest();
            request.setOldPassword("oldPassword");
            request.setNewPassword("newPassword123");
            request.setConfirmPassword("newPassword123");

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> userService.changePassword(999L, request));
            assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("成员列表查询测试")
    class GetMemberListTests {

        @Test
        @DisplayName("查询成员列表成功 - 无条件")
        void getMemberList_Success_NoFilters() {
            // Given
            Page<User> page = new Page<>(1, 10);
            page.setRecords(Arrays.asList(testUser));
            page.setTotal(1L);
            page.setCurrent(1L);
            page.setSize(10L);
            page.setPages(1L);

            when(userMapper.selectPage(any(Page.class), any())).thenReturn(page);

            // When
            MemberListResponse response = userService.getMemberList(1L, 10L, null, null);

            // Then
            assertNotNull(response);
            assertEquals(1L, response.getTotal());
            assertEquals(1L, response.getCurrent());
            assertEquals(10L, response.getSize());
            assertEquals(1, response.getRecords().size());
            assertEquals("张三", response.getRecords().get(0).getUsername());
        }

        @Test
        @DisplayName("查询成员列表成功 - 按角色筛选")
        void getMemberList_Success_WithRoleFilter() {
            // Given
            Page<User> page = new Page<>(1, 10);
            page.setRecords(Collections.singletonList(testUser));
            page.setTotal(1L);

            when(userMapper.selectPage(any(Page.class), any())).thenReturn(page);

            // When
            MemberListResponse response = userService.getMemberList(1L, 10L, "ROLE_MEMBER", null);

            // Then
            assertNotNull(response);
            assertEquals(1, response.getRecords().size());
        }

        @Test
        @DisplayName("查询成员列表成功 - 按关键词搜索")
        void getMemberList_Success_WithKeyword() {
            // Given
            Page<User> page = new Page<>(1, 10);
            page.setRecords(Collections.singletonList(testUser));
            page.setTotal(1L);

            when(userMapper.selectPage(any(Page.class), any())).thenReturn(page);

            // When
            MemberListResponse response = userService.getMemberList(1L, 10L, null, "张三");

            // Then
            assertNotNull(response);
            assertEquals(1, response.getRecords().size());
        }

        @Test
        @DisplayName("查询成员列表 - 分页边界值保护")
        void getMemberList_PageBoundaryProtection() {
            // Given
            Page<User> page1 = new Page<>(1, 10);
            page1.setRecords(Collections.emptyList());
            page1.setTotal(0L);

            Page<User> page2 = new Page<>(1, 100);
            page2.setRecords(Collections.emptyList());
            page2.setTotal(0L);

            when(userMapper.selectPage(any(Page.class), any()))
                    .thenReturn(page1)
                    .thenReturn(page2);

            // When - current < 1
            MemberListResponse response = userService.getMemberList(0L, 10L, null, null);

            // Then - 应该修正为 1
            assertNotNull(response);
            assertEquals(1L, response.getCurrent());

            // When - size > 100
            response = userService.getMemberList(1L, 200L, null, null);

            // Then - 应该修正为 100
            assertEquals(100L, response.getSize());
        }

        @Test
        @DisplayName("查询成员列表成功 - 空结果")
        void getMemberList_Success_EmptyResult() {
            // Given
            Page<User> page = new Page<>(1, 10);
            page.setRecords(Collections.emptyList());
            page.setTotal(0L);

            when(userMapper.selectPage(any(Page.class), any())).thenReturn(page);

            // When
            MemberListResponse response = userService.getMemberList(1L, 10L, null, null);

            // Then
            assertNotNull(response);
            assertEquals(0, response.getRecords().size());
            assertEquals(0L, response.getTotal());
        }
    }

    @Nested
    @DisplayName("更新用户角色测试")
    class UpdateUserRoleTests {

        @Test
        @DisplayName("更新用户角色成功")
        void updateUserRole_Success() {
            // Given
            when(userMapper.selectById(1L)).thenReturn(testUser);
            when(userMapper.updateById(any(User.class))).thenReturn(1);

            UpdateUserRoleRequest request = new UpdateUserRoleRequest();
            request.setRole("ROLE_ADMIN");

            // When
            userService.updateUserRole(1L, request);

            // Then
            verify(userMapper).updateById(any(User.class));
        }

        @Test
        @DisplayName("更新用户角色失败 - 用户不存在")
        void updateUserRole_Fail_UserNotFound() {
            // Given
            when(userMapper.selectById(999L)).thenReturn(null);

            UpdateUserRoleRequest request = new UpdateUserRoleRequest();
            request.setRole("ROLE_MEMBER");

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> userService.updateUserRole(999L, request));
            assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        }

        @Test
        @DisplayName("更新用户角色失败 - 尝试修改负责人角色")
        void updateUserRole_Fail_CannotModifyAdminRole() {
            // Given
            testUser.setRole("ROLE_ADMIN");
            when(userMapper.selectById(1L)).thenReturn(testUser);

            UpdateUserRoleRequest request = new UpdateUserRoleRequest();
            request.setRole("ROLE_MEMBER");

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> userService.updateUserRole(1L, request));
            assertEquals(ErrorCode.INVALID_ROLE, exception.getErrorCode());
            assertTrue(exception.getMessage().contains("不能修改负责人的角色"));
        }
    }

    @Nested
    @DisplayName("更新用户状态测试")
    class UpdateUserStatusTests {

        @Test
        @DisplayName("更新用户状态成功 - 禁用账号")
        void updateUserStatus_Success_Disable() {
            // Given
            when(userMapper.selectById(1L)).thenReturn(testUser);
            when(userMapper.updateById(any(User.class))).thenReturn(1);

            UpdateUserStatusRequest request = new UpdateUserStatusRequest();
            request.setStatus(0);

            // When
            userService.updateUserStatus(1L, request);

            // Then
            verify(userMapper).updateById(any(User.class));
        }

        @Test
        @DisplayName("更新用户状态成功 - 启用账号")
        void updateUserStatus_Success_Enable() {
            // Given
            testUser.setStatus(0);
            when(userMapper.selectById(1L)).thenReturn(testUser);
            when(userMapper.updateById(any(User.class))).thenReturn(1);

            UpdateUserStatusRequest request = new UpdateUserStatusRequest();
            request.setStatus(1);

            // When
            userService.updateUserStatus(1L, request);

            // Then
            verify(userMapper).updateById(any(User.class));
        }

        @Test
        @DisplayName("更新用户状态失败 - 用户不存在")
        void updateUserStatus_Fail_UserNotFound() {
            // Given
            when(userMapper.selectById(999L)).thenReturn(null);

            UpdateUserStatusRequest request = new UpdateUserStatusRequest();
            request.setStatus(0);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> userService.updateUserStatus(999L, request));
            assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        }

        @Test
        @DisplayName("更新用户状态失败 - 尝试修改负责人账号")
        void updateUserStatus_Fail_CannotModifyAdminStatus() {
            // Given
            testUser.setRole("ROLE_ADMIN");
            when(userMapper.selectById(1L)).thenReturn(testUser);

            UpdateUserStatusRequest request = new UpdateUserStatusRequest();
            request.setStatus(0);

            // When & Then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> userService.updateUserStatus(1L, request));
            assertEquals(ErrorCode.USER_STATUS_UPDATE_FAILED, exception.getErrorCode());
            assertTrue(exception.getMessage().contains("不能修改负责人的账号状态"));
        }
    }
}
