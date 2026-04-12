package com.syit.cpc.controller;

import com.syit.cpc.common.response.ApiResponse;
import com.syit.cpc.dto.request.ChangePasswordRequest;
import com.syit.cpc.dto.request.UpdateProfileRequest;
import com.syit.cpc.dto.response.UserProfileResponse;
import com.syit.cpc.security.SecurityUtils;
import com.syit.cpc.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 个人资料控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "个人资料管理", description = "用户个人信息查看和修改接口")
public class ProfileController {

    private final UserService userService;
    private final SecurityUtils securityUtils;

    /**
     * 获取当前用户个人信息
     */
    @GetMapping
    @Operation(summary = "获取个人信息", description = "获取当前登录用户的个人信息")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未认证")
    })
    public ApiResponse<UserProfileResponse> getProfile() {
        Long userId = securityUtils.getCurrentUserId();
        log.info("获取用户个人信息: userId={}", userId);
        UserProfileResponse profile = userService.getUserProfile(userId);
        return ApiResponse.success(profile);
    }

    /**
     * 修改个人信息
     */
    @PutMapping
    @Operation(summary = "修改个人信息", description = "修改当前用户的昵称和个人简介")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "修改成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "参数错误")
    })
    public ApiResponse<Void> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        log.info("修改用户个人信息: userId={}", userId);
        userService.updateProfile(userId, request);
        return ApiResponse.success("个人信息已更新", null);
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    @Operation(summary = "修改密码", description = "修改当前用户的登录密码")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "密码修改成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "参数错误或旧密码错误")
    })
    public ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        log.info("用户修改密码: userId={}", userId);
        userService.changePassword(userId, request);
        return ApiResponse.success("密码修改成功，请重新登录", null);
    }
}
