package com.syit.cpc.controller;

import com.syit.cpc.common.response.ApiResponse;
import com.syit.cpc.dto.request.SubmitProjectRequest;
import com.syit.cpc.dto.response.PromotionInfoResponse;
import com.syit.cpc.dto.response.PromotionStatusResponse;
import com.syit.cpc.security.SecurityUtils;
import com.syit.cpc.service.PromotionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 转正申请控制器
 * 提供转正流程说明、状态查询、申请提交接口
 * 注意：/info 接口对所有认证用户开放，其他接口仅 ROLE_PROBATION 角色可访问
 */
@Slf4j
@RestController
@RequestMapping("/api/promotion")
@RequiredArgsConstructor
@Tag(name = "转正申请", description = "转正流程说明、状态查询、申请提交接口")
public class PromotionController {

    private final PromotionService promotionService;
    private final SecurityUtils securityUtils;

    @GetMapping("/info")
    @Operation(summary = "获取转正流程说明", description = "返回转正流程步骤和考核标准（所有认证用户可访问）")
    public ApiResponse<PromotionInfoResponse> getPromotionInfo() {
        log.info("获取转正流程说明");
        PromotionInfoResponse info = promotionService.getPromotionInfo();
        return ApiResponse.success(info);
    }

    @GetMapping("/status")
    @PreAuthorize("hasRole('PROBATION')")
    @Operation(summary = "获取当前转正状态", description = "查询当前用户的转正申请状态（仅预备成员）")
    public ApiResponse<PromotionStatusResponse> getPromotionStatus() {
        Long userId = securityUtils.getCurrentUserId();
        log.info("获取转正状态: userId={}", userId);
        PromotionStatusResponse status = promotionService.getPromotionStatus(userId);
        return ApiResponse.success(status);
    }
    
    @PostMapping("/apply")
    @PreAuthorize("hasRole('PROBATION')")
    @Operation(summary = "提交转正申请", description = "创建转正申请记录，开始转正考核（仅预备成员）")
    public ApiResponse<PromotionStatusResponse> submitPromotion() {
        Long userId = securityUtils.getCurrentUserId();
        log.info("提交转正申请: userId={}", userId);
        PromotionStatusResponse result = promotionService.submitPromotion(userId);
        return ApiResponse.success("申请成功，可以开始答题", result);
    }

    @PostMapping("/project")
    @PreAuthorize("hasRole('PROBATION')")
    @Operation(summary = "提交项目", description = "提交项目作品，进入评审流程（仅预备成员）")
    public ApiResponse<PromotionStatusResponse> submitProject(@Valid @RequestBody SubmitProjectRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        log.info("提交项目: userId={}, projectName={}", userId, request.getProjectName());
        PromotionStatusResponse result = promotionService.submitProject(userId, request);
        return ApiResponse.success("项目提交成功，等待负责人评审", result);
    }
}
