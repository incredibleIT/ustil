package com.syit.cpc.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 更新内容请求 DTO
 * 
 * 更新语义：
 * - null: 不修改该字段
 * - 空字符串 "": 清空该字段（适用于 summary、coverImage）
 * - 非空值: 更新为该值
 */
@Data
@Schema(description = "更新内容请求")
public class UpdateContentRequest {

    @Schema(description = "标题", example = "更新后的标题")
    @Size(min = 5, max = 100, message = "标题长度必须在5-100字符之间")
    private String title;

    @Schema(description = "摘要", example = "更新后的摘要")
    @Size(max = 200, message = "摘要最多200字符")
    private String summary;

    @Schema(description = "Markdown 内容")
    private String content;

    @Schema(description = "封面图 URL")
    private String coverImage;

    @Schema(description = "标签数组")
    private List<String> tags;
}
