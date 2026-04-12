package com.syit.cpc.controller;

import com.syit.cpc.common.response.ApiResponse;
import com.syit.cpc.dto.request.CreateQuestionRequest;
import com.syit.cpc.dto.request.UpdateQuestionRequest;
import com.syit.cpc.dto.response.QuestionListResponse;
import com.syit.cpc.dto.response.QuestionResponse;
import com.syit.cpc.service.QuestionService;
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

import java.util.Map;

/**
 * 题库管理控制器（仅负责人可访问）
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/questions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "管理员-题库管理", description = "题目增删改查接口")
public class QuestionAdminController {

    private final QuestionService questionService;

    @GetMapping
    @Operation(summary = "分页查询题目列表", description = "支持题型筛选和关键词搜索")
    public ApiResponse<QuestionListResponse> getQuestionList(
            @Parameter(description = "当前页码", example = "1") @RequestParam(defaultValue = "1") @Min(1) Long current,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") @Min(1) @Max(100) Long size,
            @Parameter(description = "题型筛选", example = "single_choice") @RequestParam(required = false) String type,
            @Parameter(description = "搜索关键词", example = "Java") @RequestParam(required = false) String keyword) {
        
        log.info("获取题目列表: page={}, size={}, type={}, keyword={}", current, size, type, keyword);
        QuestionListResponse questionList = questionService.getQuestionList(current, size, type, keyword);
        return ApiResponse.success(questionList);
    }

    @PostMapping
    @Operation(summary = "创建题目", description = "添加新题目到题库")
    public ApiResponse<QuestionResponse> createQuestion(@Valid @RequestBody CreateQuestionRequest request) {
        log.info("创建题目: type={}", request.getQuestionType());
        QuestionResponse question = questionService.createQuestion(request);
        return ApiResponse.success("题目已创建", question);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新题目", description = "修改题目信息")
    public ApiResponse<QuestionResponse> updateQuestion(
            @Parameter(description = "题目ID", required = true) @PathVariable Long id,
            @Valid @RequestBody UpdateQuestionRequest request) {
        
        log.info("更新题目: id={}", id);
        QuestionResponse question = questionService.updateQuestion(id, request);
        return ApiResponse.success("题目已更新", question);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除题目", description = "软删除题目")
    public ApiResponse<Void> deleteQuestion(
            @Parameter(description = "题目ID", required = true) @PathVariable Long id) {
        
        log.info("删除题目: id={}", id);
        questionService.deleteQuestion(id);
        return ApiResponse.success("题目已删除", null);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取题目详情", description = "查看单个题目完整信息")
    public ApiResponse<QuestionResponse> getQuestionDetail(
            @Parameter(description = "题目ID", required = true) @PathVariable Long id) {
        
        log.info("获取题目详情: id={}", id);
        QuestionResponse question = questionService.getQuestionDetail(id);
        return ApiResponse.success(question);
    }

    @GetMapping("/stats")
    @Operation(summary = "获取题目统计信息", description = "获取各题型数量统计")
    public ApiResponse<Map<String, Long>> getQuestionStats() {
        log.info("获取题目统计信息");
        Map<String, Long> stats = questionService.getQuestionStats();
        return ApiResponse.success(stats);
    }
}
