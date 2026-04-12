package com.syit.cpc.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户个人信息响应 DTO
 */
@Data
@Schema(description = "用户个人信息响应")
public class UserProfileResponse {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "学号")
    private String studentId;

    @Schema(description = "用户名/昵称")
    private String username;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "角色")
    private String role;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "个人简介")
    private String bio;

    @Schema(description = "账号状态: 0-禁用, 1-启用")
    private Integer status;

    @Schema(description = "加入时间")
    private LocalDateTime createdAt;
}
