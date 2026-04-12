package com.syit.cpc.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户内容列表项响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserContentResponse {
    private Long id;              // 内容 ID
    private String title;         // 标题
    private String summary;       // 摘要
    private String type;          // 类型：news-资讯, blog-博客
    private String coverImage;    // 封面图 URL
    private String status;        // 状态：pending-审核中, published-已发布, rejected-已拒绝
    private LocalDateTime createdAt;  // 创建时间
}
