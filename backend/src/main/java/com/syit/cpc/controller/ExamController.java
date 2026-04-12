package com.syit.cpc.controller;

import com.syit.cpc.common.exception.BusinessException;
import com.syit.cpc.common.exception.ErrorCode;
import com.syit.cpc.common.response.ApiResponse;
import com.syit.cpc.dto.request.SubmitExamRequest;
import com.syit.cpc.dto.response.ExamResultResponse;
import com.syit.cpc.dto.response.PaperResponse;
import com.syit.cpc.security.JwtTokenProvider;
import com.syit.cpc.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 考试控制器（预备成员可访问）
 */
@Slf4j
@RestController
@RequestMapping("/api/exam")
@RequiredArgsConstructor
@PreAuthorize("hasRole('PROBATION')")
@Tag(name = "考试管理", description = "在线答题相关接口")
public class ExamController {

    private final QuestionService questionService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/generate-paper")
    @Operation(summary = "随机生成试卷", description = "从题库中随机抽取题目生成试卷")
    public ApiResponse<PaperResponse> generatePaper(HttpServletRequest request) {
        // 从请求头中获取 JWT token
        String token = extractToken(request);
        if (token == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        // 从 JWT 中获取用户ID
        String userIdStr = jwtTokenProvider.getUserIdFromToken(token);
        Long userId = Long.parseLong(userIdStr);
        
        log.info("生成试卷: userId={}", userId);
        PaperResponse paper = questionService.generatePaper(userId);
        return ApiResponse.success(paper);
    }

    @PostMapping("/submit")
    @Operation(summary = "提交试卷", description = "提交答案并自动判卷")
    public ApiResponse<ExamResultResponse> submitExam(
            @Valid @RequestBody SubmitExamRequest request,
            HttpServletRequest httpRequest) {
        // 从请求头中获取 JWT token
        String token = extractToken(httpRequest);
        if (token == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        // 从 JWT 中获取用户ID
        String userIdStr = jwtTokenProvider.getUserIdFromToken(token);
        Long userId = Long.parseLong(userIdStr);
        
        log.info("提交试卷: userId={}, paperId={}", userId, request.getPaperId());
        ExamResultResponse result = questionService.submitExam(userId, request);
        return ApiResponse.success("试卷提交成功", result);
    }

    @GetMapping("/result")
    @Operation(summary = "获取考试结果", description = "获取用户最新考试结果")
    public ApiResponse<ExamResultResponse> getExamResult(HttpServletRequest request) {
        // 从请求头中获取 JWT token
        String token = extractToken(request);
        if (token == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        // 从 JWT 中获取用户ID
        String userIdStr = jwtTokenProvider.getUserIdFromToken(token);
        Long userId = Long.parseLong(userIdStr);
        
        log.info("获取考试结果: userId={}", userId);
        ExamResultResponse result = questionService.getLatestExamResult(userId);
        return ApiResponse.success(result);
    }

    /**
     * 从请求头中提取 JWT token
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
