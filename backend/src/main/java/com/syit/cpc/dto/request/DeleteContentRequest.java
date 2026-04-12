package com.syit.cpc.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 删除内容请求 DTO
 */
@Data
@Schema(description = "删除内容请求")
public class DeleteContentRequest {

    @Schema(description = "删除原因（管理员可选）", example = "违反社区规范")
    private String reason;
}
