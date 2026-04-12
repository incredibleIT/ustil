package com.syit.cpc.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新用户状态请求 DTO
 */
@Data
@Schema(description = "更新用户状态请求")
public class UpdateUserStatusRequest {

    @Schema(description = "状态: 0-禁用, 1-启用")
    @NotNull(message = "状态不能为空")
    private Integer status;
}
