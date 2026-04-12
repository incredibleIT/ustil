package com.syit.cpc.controller;

import com.syit.cpc.common.response.ApiResponse;
import com.syit.cpc.dto.request.DeleteContentRequest;
import com.syit.cpc.dto.request.PublishContentRequest;
import com.syit.cpc.dto.request.UpdateContentRequest;
import com.syit.cpc.dto.response.ContentDetailResponse;
import com.syit.cpc.dto.response.PublishContentResponse;
import com.syit.cpc.dto.response.UserContentResponse;
import com.syit.cpc.security.SecurityUtils;
import com.syit.cpc.service.ContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 内容控制器
 * 提供内容发布、编辑、删除等接口
 * 仅认证用户（ROLE_MEMBER 或 ROLE_PROBATION）可访问
 */
@Slf4j
@RestController
@RequestMapping("/api/content")
@RequiredArgsConstructor
@Tag(name = "内容管理", description = "内容发布、编辑、删除接口")
public class ContentController {

    private final ContentService contentService;
    private final SecurityUtils securityUtils;

    @PostMapping
    @PreAuthorize("isAuthenticated()") // 临时放宽：任何已认证用户都可发布
    // @PreAuthorize("hasAnyRole('MEMBER', 'PROBATION')") // 原权限配置
    @Operation(summary = "发布内容", description = "发布资讯或博客（仅认证用户）")
    public ApiResponse<PublishContentResponse> publishContent(@Valid @RequestBody PublishContentRequest request) {
        Long authorId = securityUtils.getCurrentUserId();
        log.info("发布内容: userId={}, type={}, title={}", authorId, request.getType(), request.getTitle());
        
        ApiResponse<PublishContentResponse> response = contentService.publishContent(request, authorId);
        
        if (response.isSuccess()) {
            log.info("发布成功: contentId={}", response.getData().getId());
        } else {
            log.warn("发布失败: {}", response.getMessage());
        }
        
        return response;
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "获取我的内容", description = "获取当前用户发布的所有内容列表")
    public ApiResponse<List<UserContentResponse>> getMyContents() {
        Long userId = securityUtils.getCurrentUserId();
        log.info("获取用户内容列表: userId={}", userId);
        
        return contentService.getUserContents(userId);
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取所有内容（管理员）", description = "管理员获取所有用户的内容列表")
    public ApiResponse<List<UserContentResponse>> getAllContents() {
        log.info("管理员获取所有内容列表");
        return contentService.getAllContents();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "获取内容详情", description = "获取指定内容的详细信息")
    public ApiResponse<ContentDetailResponse> getContentById(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        log.info("获取内容详情: contentId={}, userId={}", id, userId);
        
        return contentService.getContentById(id, userId);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "更新内容", description = "更新已发布或审核中的内容")
    public ApiResponse<PublishContentResponse> updateContent(
        @PathVariable Long id,
        @Valid @RequestBody UpdateContentRequest request
    ) {
        Long userId = securityUtils.getCurrentUserId();
        log.info("更新内容: contentId={}, userId={}", id, userId);
        
        ApiResponse<PublishContentResponse> response = contentService.updateContent(id, request, userId);
        
        if (response.isSuccess()) {
            log.info("更新成功: contentId={}, newStatus={}", id, response.getData().getStatus());
        } else {
            log.warn("更新失败: {}", response.getMessage());
        }
        
        return response;
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "删除内容", description = "删除内容（逻辑删除）")
    public ApiResponse<Void> deleteContent(
        @PathVariable Long id,
        @RequestBody(required = false) DeleteContentRequest request
    ) {
        Long userId = securityUtils.getCurrentUserId();
        String reason = request != null ? request.getReason() : null;
        
        log.info("删除内容: contentId={}, userId={}, reason={}", id, userId, reason);
        
        ApiResponse<Void> response = contentService.deleteContent(id, userId, reason);
        
        if (response.isSuccess()) {
            log.info("删除成功: contentId={}", id);
        } else {
            log.warn("删除失败: {}", response.getMessage());
        }
        
        return response;
    }
}
