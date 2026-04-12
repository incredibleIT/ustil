package com.syit.cpc.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 发布内容请求 DTO
 */
@Data
public class PublishContentRequest {

    /**
     * 标题（必填，5-100字符）
     */
    @NotBlank(message = "标题不能为空")
    @Size(min = 5, max = 100, message = "标题长度为 5-100 个字符")
    private String title;

    /**
     * 摘要（可选，最多200字符）
     */
    @Size(max = 200, message = "摘要最多 200 个字符")
    private String summary;

    /**
     * Markdown 内容（必填）
     */
    @NotBlank(message = "内容不能为空")
    private String content;

    /**
     * 类型：news-资讯, blog-博客（必填）
     */
    @NotBlank(message = "类型不能为空")
    private String type;

    /**
     * 封面图 URL（可选）
     */
    private String coverImage;

    /**
     * 标签数组（可选）
     */
    private List<String> tags;
}
