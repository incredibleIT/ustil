package com.syit.cpc.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 内容详情响应 DTO
 */
@Data
@Schema(description = "内容详情响应")
public class ContentDetailResponse {

    @Schema(description = "内容 ID", example = "123")
    private Long id;

    @Schema(description = "标题", example = "我的博客文章")
    private String title;

    @Schema(description = "摘要", example = "这是一篇关于...")
    private String summary;

    @Schema(description = "Markdown 内容")
    private String content;

    @Schema(description = "类型", example = "blog", allowableValues = {"news", "blog"})
    private String type;

    @Schema(description = "状态", example = "pending", allowableValues = {"draft", "pending", "published", "rejected"})
    private String status;

    @Schema(description = "封面图 URL")
    private String coverImage;

    @Schema(description = "标签数组")
    private List<String> tags;

    @Schema(description = "作者 ID", example = "456")
    private Long authorId;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
