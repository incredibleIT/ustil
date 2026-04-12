package com.syit.cpc.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 更新用户角色请求 DTO
 */
@Data
@Schema(description = "更新用户角色请求")
public class UpdateUserRoleRequest {

    @Schema(description = "角色: ROLE_PROBATION/ROLE_MEMBER/ROLE_ADMIN")
    @NotBlank(message = "角色不能为空")
    private String role;
}
