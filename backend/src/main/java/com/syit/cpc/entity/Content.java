package com.syit.cpc.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 内容实体类
 * 对应数据库表: content
 */
@Data
@TableName(value = "content", autoResultMap = true)
public class Content {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 摘要
     */
    private String summary;

    /**
     * 内容(Markdown格式)
     */
    private String content;

    /**
     * 类型: news-资讯, blog-博客
     */
    private String type;

    /**
     * 状态: draft-草稿, pending-审核中, published-已发布, rejected-已拒绝
     */
    private String status;

    /**
     * 作者ID
     */
    private Long authorId;

    /**
     * 封面图URL
     */
    private String coverImage;

    /**
     * 标签数组
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> tags;

    /**
     * 阅读数
     */
    private Integer viewCount;

    /**
     * 逻辑删除: 0-未删除, 1-已删除
     */
    @TableLogic
    private Integer deleted;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}

