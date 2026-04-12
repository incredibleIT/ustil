package com.syit.cpc.dto.request;

import com.syit.cpc.common.validation.PasswordMatch;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 修改密码请求 DTO
 */
@Data
@Schema(description = "修改密码请求")
@PasswordMatch(password = "newPassword", confirmPassword = "confirmPassword", message = "两次输入的密码不一致")
public class ChangePasswordRequest {

    @Schema(description = "旧密码")
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    @Schema(description = "新密码")
    @NotBlank(message = "新密码不能为空")
    @Size(min = 8, message = "密码长度至少8位")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "密码必须包含字母和数字")
    private String newPassword;

    @Schema(description = "确认新密码")
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
}
