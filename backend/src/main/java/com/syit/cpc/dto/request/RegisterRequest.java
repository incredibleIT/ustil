package com.syit.cpc.dto.request;

import com.syit.cpc.common.validation.PasswordMatch;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * 用户注册请求 DTO
 */
@Data
@PasswordMatch(password = "password", confirmPassword = "confirmPassword", message = "两次输入的密码不一致")
@Schema(description = "用户注册请求")
public class RegisterRequest {
    
    @NotBlank(message = "学号不能为空")
    @Pattern(regexp = "^\\d{9,12}$", message = "学号格式不正确，应为9-12位数字")
    @Schema(description = "学号", example = "2021001234")
    private String studentId;
    
    @NotBlank(message = "姓名不能为空")
    @Size(min = 2, max = 50, message = "姓名长度应为2-50个字符")
    @Schema(description = "姓名", example = "张三")
    private String username;
    
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱", example = "user@example.com")
    private String email;
    
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 100, message = "密码长度至少8位")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "密码必须包含字母和数字")
    @Schema(description = "密码")
    private String password;
    
    @NotBlank(message = "确认密码不能为空")
    @Schema(description = "确认密码")
    private String confirmPassword;
}
