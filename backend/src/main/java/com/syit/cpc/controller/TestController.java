package com.syit.cpc.controller;

import com.syit.cpc.common.exception.BusinessException;
import com.syit.cpc.common.exception.ErrorCode;
import com.syit.cpc.common.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 测试控制器
 * 用于验证统一响应格式和异常处理
 */
@RestController
@RequestMapping("/api/test")
public class TestController {

    /**
     * 测试成功响应
     *
     * @return ApiResponse
     */
    @GetMapping("/success")
    @PreAuthorize("permitAll()")
    public ApiResponse<TestData> testSuccess() {
        TestData data = new TestData();
        data.setId(1L);
        data.setName("测试数据");
        return ApiResponse.success(data);
    }

    /**
     * 测试自定义成功消息
     *
     * @return ApiResponse
     */
    @GetMapping("/success-with-message")
    public ApiResponse<TestData> testSuccessWithMessage() {
        TestData data = new TestData();
        data.setId(2L);
        data.setName("自定义消息测试");
        return ApiResponse.success("操作成功完成", data);
    }

    /**
     * 测试业务异常
     *
     * @return ApiResponse
     */
    @GetMapping("/business-error")
    public ApiResponse<Void> testBusinessError() {
        throw new BusinessException(ErrorCode.BUSINESS_ERROR, "业务逻辑错误：用户状态异常");
    }

    /**
     * 测试参数校验异常
     *
     * @param request 请求参数
     * @return ApiResponse
     */
    @PostMapping("/validation")
    public ApiResponse<TestData> testValidation(@Valid @RequestBody TestRequest request) {
        TestData data = new TestData();
        data.setId(3L);
        data.setName(request.getName());
        return ApiResponse.success(data);
    }

    /**
     * 测试 404 处理
     * 注意：此方法不会被执行，用于测试不存在的路径
     */
    @GetMapping("/not-found")
    public ApiResponse<Void> testNotFound() {
        // 这个方法存在，但可以通过访问不存在的路径测试 404
        return ApiResponse.success(null);
    }

    /**
     * 测试内部服务器错误
     *
     * @return ApiResponse
     */
    @GetMapping("/internal-error")
    public ApiResponse<Void> testInternalError() {
        throw new RuntimeException("模拟系统内部错误");
    }

    /**
     * 测试数据类
     */
    @Data
    public static class TestData {
        private Long id;
        private String name;
    }

    /**
     * 测试请求类
     */
    @Data
    public static class TestRequest {
        @NotBlank(message = "名称不能为空")
        private String name;

        @NotBlank(message = "描述不能为空")
        private String description;
    }
}
