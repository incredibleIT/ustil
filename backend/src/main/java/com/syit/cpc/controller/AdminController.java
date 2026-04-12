package com.syit.cpc.controller;

import com.syit.cpc.common.response.ApiResponse;
import com.syit.cpc.dto.request.ReviewProjectRequest;
import com.syit.cpc.dto.request.UpdateUserRoleRequest;
import com.syit.cpc.dto.request.UpdateUserStatusRequest;
import com.syit.cpc.dto.response.MemberListResponse;
import com.syit.cpc.dto.response.PromotionReviewListResponse;
import com.syit.cpc.dto.response.PromotionStatusResponse;
import com.syit.cpc.security.SecurityUtils;
import com.syit.cpc.service.PromotionService;
import com.syit.cpc.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员控制器
 * 提供成员管理相关接口（仅 ADMIN 角色可访问）
 */
@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "管理员-成员与转正管理", description = "成员列表查看、角色修改、状态管理、转正评审接口")
public class AdminController {

    private final UserService userService;
    private final PromotionService promotionService;
    private final SecurityUtils securityUtils;

    @GetMapping("/members")
    @Operation(summary = "获取成员列表", description = "分页查询成员列表，支持角色筛选和关键词搜索")
    public ApiResponse<MemberListResponse> getMemberList(
            @Parameter(description = "当前页码", example = "1") @RequestParam(defaultValue = "1") @Min(1) Long current,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") @Min(1) @Max(100) Long size,
            @Parameter(description = "角色筛选", example = "ROLE_MEMBER") @RequestParam(required = false) String role,
            @Parameter(description = "搜索关键词", example = "张三") @RequestParam(required = false) String keyword) {
        
        log.info("获取成员列表: page={}, size={}, role={}, keyword={}", current, size, role, keyword);
        MemberListResponse memberList = userService.getMemberList(current, size, role, keyword);
        return ApiResponse.success(memberList);
    }

    @PutMapping("/{userId}/role")
    @Operation(summary = "修改成员角色", description = "修改指定成员的角色（预备成员/正式成员/负责人）")
    public ApiResponse<Void> updateUserRole(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRoleRequest request) {
        
        log.info("修改成员角色: userId={}, role={}", userId, request.getRole());
        userService.updateUserRole(userId, request);
        return ApiResponse.success("角色已更新", null);
    }

    @PutMapping("/{userId}/status")
    @Operation(summary = "更新成员状态", description = "禁用或启用指定成员的账号")
    public ApiResponse<Void> updateUserStatus(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId,
            @Valid @RequestBody UpdateUserStatusRequest request) {
        
        log.info("更新成员状态: userId={}, status={}", userId, request.getStatus());
        userService.updateUserStatus(userId, request);
        return ApiResponse.success("账号已更新", null);
    }

    // ==================== 转正评审接口 ====================

    @GetMapping("/promotion/pending")
    @Operation(summary = "获取待评审列表", description = "分页查询待评审的转正申请（仅负责人）")
    public ApiResponse<PromotionReviewListResponse> getPendingPromotions(
            @Parameter(description = "当前页码", example = "1") @RequestParam(defaultValue = "1") @Min(1) int page,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @Parameter(description = "状态筛选", example = "pending_project") @RequestParam(required = false) String status) {
        
        log.info("获取待评审列表: page={}, size={}, status={}", page, size, status);
        PromotionReviewListResponse result = promotionService.getPendingReviews(page, size, status);
        return ApiResponse.success(result);
    }

    @PostMapping("/promotion/review")
    @Operation(summary = "评审项目", description = "对转正申请进行评审并计算总分（仅负责人）")
    public ApiResponse<PromotionStatusResponse> reviewProject(@Valid @RequestBody ReviewProjectRequest request) {
        Long adminId = securityUtils.getCurrentUserId();
        log.info("评审项目: adminId={}, applicationId={}", adminId, request.getApplicationId());
        PromotionStatusResponse result = promotionService.reviewProject(adminId, request);
        return ApiResponse.success("评审完成", result);
    }
}
