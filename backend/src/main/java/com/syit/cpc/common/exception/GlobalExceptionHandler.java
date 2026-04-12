package com.syit.cpc.common.exception;

import com.syit.cpc.common.response.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 统一处理所有 Controller 层抛出的异常
 */
@Slf4j
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     *
     * @param e BusinessException
     * @return ApiResponse
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return ApiResponse.error(e.getErrorCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常（@Valid 校验失败）
     *
     * @param e MethodArgumentNotValidException
     * @return ApiResponse
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数校验失败: {}", message);
        return ApiResponse.error(ErrorCode.PARAM_ERROR, "参数错误：" + message);
    }

    /**
     * 处理约束校验异常（@Validated 校验失败）
     *
     * @param e ConstraintViolationException
     * @return ApiResponse
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        log.warn("约束校验失败: {}", message);
        return ApiResponse.error(ErrorCode.PARAM_ERROR, "参数错误：" + message);
    }

    /**
     * 处理 404 未找到处理器异常
     *
     * @param e NoHandlerFoundException
     * @return ApiResponse
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.warn("请求路径不存在: {}", e.getRequestURL());
        return ApiResponse.error(ErrorCode.NOT_FOUND, "请求路径不存在：" + e.getRequestURL());
    }

    /**
     * 处理非法参数异常
     *
     * @param e IllegalArgumentException
     * @return ApiResponse
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("非法参数: {}", e.getMessage());
        return ApiResponse.error(ErrorCode.PARAM_ERROR, e.getMessage());
    }

    /**
     * 处理非法状态异常
     *
     * @param e IllegalStateException
     * @return ApiResponse
     */
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleIllegalStateException(IllegalStateException e) {
        log.warn("非法状态: {}", e.getMessage());
        return ApiResponse.error(ErrorCode.BUSINESS_ERROR, e.getMessage());
    }

    /**
     * 处理所有其他未捕获的异常
     *
     * @param e Exception
     * @return ApiResponse
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleException(Exception e) {
        log.error("系统内部错误: ", e);
        return ApiResponse.error(ErrorCode.INTERNAL_ERROR, "系统繁忙，请稍后重试");
    }
}
