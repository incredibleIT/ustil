package com.syit.cpc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.syit.cpc.common.exception.ErrorCode;
import com.syit.cpc.common.response.ApiResponse;
import com.syit.cpc.dto.request.PublishContentRequest;
import com.syit.cpc.dto.request.UpdateContentRequest;
import com.syit.cpc.dto.response.ContentDetailResponse;
import com.syit.cpc.dto.response.PublishContentResponse;
import com.syit.cpc.dto.response.UserContentResponse;
import com.syit.cpc.entity.Content;
import com.syit.cpc.mapper.ContentMapper;
import com.syit.cpc.security.SecurityUtils;
import com.syit.cpc.service.ContentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 内容服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContentServiceImpl implements ContentService {

    private final ContentMapper contentMapper;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional
    public ApiResponse<PublishContentResponse> publishContent(PublishContentRequest request, Long authorId) {
        // 验证类型
        if (!"news".equals(request.getType()) && !"blog".equals(request.getType())) {
            return ApiResponse.error(ErrorCode.PARAM_ERROR, "类型必须是 news 或 blog");
        }

        // 创建内容实体
        Content content = new Content();
        content.setTitle(request.getTitle());
        
        // 如果未提供摘要，自动生成（截取内容前 200 字符）
        String summary = request.getSummary();
        if (summary == null || summary.trim().isEmpty()) {
            summary = generateSummary(request.getContent());
        }
        content.setSummary(summary);
        
        content.setContent(request.getContent());
        content.setType(request.getType());
        content.setCoverImage(request.getCoverImage());
        content.setStatus("pending"); // 初始状态：审核中
        content.setAuthorId(authorId);
        content.setViewCount(0);
        content.setTags(request.getTags());
        // createdAt 和 updatedAt 由 MyBatis-Plus 自动填充

        // 保存到数据库
        contentMapper.insert(content);

        // 构建响应
        PublishContentResponse response = new PublishContentResponse();
        response.setId(content.getId());
        response.setStatus(content.getStatus());

        return ApiResponse.success("发布成功", response);
    }

    /**
     * 自动生成摘要（截取内容前 200 字符）
     *
     * @param markdownContent Markdown 内容
     * @return 纯文本摘要
     */
    private String generateSummary(String markdownContent) {
        if (markdownContent == null || markdownContent.isEmpty()) {
            return "";
        }
    
        // 更完善的 Markdown 清理
        String plainText = markdownContent
            // 移除代码块
            .replaceAll("```[\\s\\S]*?```", "")
            // 移除行内代码
            .replaceAll("`[^`]+`", "")
            // 移除图片
            .replaceAll("!\\[[^\\]]*\\]\\([^)]+\\)", "")
            // 移除链接，保留文本
            .replaceAll("\\[([^\\]]+)\\]\\([^)]+\\)", "$1")
            // 移除标题标记
            .replaceAll("^#+\\s*", "")
            // 移除粗体/斜体
            .replaceAll("[*_]{1,3}", "")
            // 移除引用标记
            .replaceAll("^>\\s*", "")
            // 移除列表标记
            .replaceAll("^\\s*[-*+]\\s+", "")
            .replaceAll("^\\s*\\d+\\.\\s+", "")
            // 移除水平线
            .replaceAll("^\\s*[-*_]{3,}\\s*$", "")
            // 合并多余空白
            .replaceAll("\\n+", " ")
            .replaceAll("\\s+", " ")
            .trim();
    
        // 截取前 200 字符
        return plainText.length() > 200 ? plainText.substring(0, 200) : plainText;
    }

    @Override
    public ApiResponse<List<UserContentResponse>> getUserContents(Long userId) {
        // 查询用户的所有内容，按创建时间倒序
        LambdaQueryWrapper<Content> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Content::getAuthorId, userId)
               .orderByDesc(Content::getCreatedAt);
        
        List<Content> contents = contentMapper.selectList(wrapper);
        
        // 转换为响应 DTO
        List<UserContentResponse> responseList = contents.stream()
            .map(content -> UserContentResponse.builder()
                .id(content.getId())
                .title(content.getTitle())
                .summary(content.getSummary())
                .type(content.getType())
                .coverImage(content.getCoverImage())
                .status(content.getStatus())
                .createdAt(content.getCreatedAt())
                .build())
            .collect(Collectors.toList());
        
        return ApiResponse.success(responseList);
    }

    @Override
    public ApiResponse<List<UserContentResponse>> getAllContents() {
        // 查询所有内容，按创建时间倒序
        LambdaQueryWrapper<Content> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Content::getCreatedAt);
        
        List<Content> contents = contentMapper.selectList(wrapper);
        
        // 转换为响应 DTO
        List<UserContentResponse> responseList = contents.stream()
            .map(content -> UserContentResponse.builder()
                .id(content.getId())
                .title(content.getTitle())
                .summary(content.getSummary())
                .type(content.getType())
                .status(content.getStatus())
                .createdAt(content.getCreatedAt())
                .build())
            .collect(Collectors.toList());
        
        return ApiResponse.success(responseList);
    }

    @Override
    public ApiResponse<ContentDetailResponse> getContentById(Long contentId, Long userId) {
        Content content = contentMapper.selectById(contentId);

        if (content == null) {
            return ApiResponse.error(ErrorCode.NOT_FOUND, "内容不存在");
        }

        // 验证权限：作者或管理员可以查看
        boolean isAuthor = content.getAuthorId().equals(userId);
        boolean isAdmin = securityUtils.isAdmin();
        
        if (!isAuthor && !isAdmin) {
            return ApiResponse.error(ErrorCode.FORBIDDEN);
        }

        // 转换为响应 DTO
        ContentDetailResponse response = new ContentDetailResponse();
        response.setId(content.getId());
        response.setTitle(content.getTitle());
        response.setSummary(content.getSummary());
        response.setContent(content.getContent());
        response.setType(content.getType());
        response.setStatus(content.getStatus());
        response.setCoverImage(content.getCoverImage());
        response.setTags(content.getTags());
        response.setAuthorId(content.getAuthorId());
        response.setCreatedAt(content.getCreatedAt());
        response.setUpdatedAt(content.getUpdatedAt());

        return ApiResponse.success(response);
    }

    @Override
    @Transactional
    public ApiResponse<PublishContentResponse> updateContent(Long contentId, UpdateContentRequest request, Long userId) {
        Content content = contentMapper.selectById(contentId);

        if (content == null) {
            return ApiResponse.error(ErrorCode.NOT_FOUND, "内容不存在");
        }

        // 验证权限：作者或管理员可以编辑
        boolean isAuthor = content.getAuthorId().equals(userId);
        boolean isAdmin = securityUtils.isAdmin();
        
        if (!isAuthor && !isAdmin) {
            return ApiResponse.error(ErrorCode.FORBIDDEN);
        }

        // 验证状态：只能编辑 pending 或 published 状态的内容
        if ("rejected".equals(content.getStatus())) {
            return ApiResponse.error(ErrorCode.PARAM_ERROR, "已被拒绝的内容无法编辑");
        }

        // 更新字段（只更新非 null 字段）
        if (request.getTitle() != null) {
            content.setTitle(request.getTitle());
        }
        if (request.getSummary() != null) {
            content.setSummary(request.getSummary());
        }
        if (request.getContent() != null) {
            content.setContent(request.getContent());
        }
        if (request.getCoverImage() != null) {
            content.setCoverImage(request.getCoverImage());
        }
        if (request.getTags() != null) {
            content.setTags(request.getTags());
        }

        // 如果原来是 published，更新后重新进入审核
        if ("published".equals(content.getStatus())) {
            content.setStatus("pending");
        }
        // 如果原来是 pending，保持 pending

        contentMapper.updateById(content);

        PublishContentResponse response = new PublishContentResponse();
        response.setId(content.getId());
        response.setStatus(content.getStatus());

        return ApiResponse.success("更新成功", response);
    }

    @Override
    @Transactional
    public ApiResponse<Void> deleteContent(Long contentId, Long userId, String reason) {
        Content content = contentMapper.selectById(contentId);

        if (content == null) {
            return ApiResponse.error(ErrorCode.NOT_FOUND, "内容不存在");
        }

        // 检查权限：作者或管理员
        boolean isAuthor = content.getAuthorId().equals(userId);
        boolean isAdmin = securityUtils.isAdmin();

        if (!isAuthor && !isAdmin) {
            return ApiResponse.error(ErrorCode.FORBIDDEN);
        }

        // 管理员删除必须填写原因
        if (isAdmin && (reason == null || reason.trim().isEmpty())) {
            return ApiResponse.error(ErrorCode.PARAM_ERROR, "管理员删除内容必须填写原因");
        }

        // 记录删除操作日志（TODO: 实现审计日志功能）
        if (isAdmin && reason != null) {
            log.info("管理员删除内容: contentId={}, adminId={}, reason={}", contentId, userId, reason);
            // TODO: 保存到 audit_log 表
        } else if (isAuthor) {
            log.info("用户删除自己的内容: contentId={}, authorId={}", contentId, userId);
        }

        // 逻辑删除（MyBatis-Plus 自动处理 @TableLogic）
        contentMapper.deleteById(contentId);

        return ApiResponse.success("删除成功", null);
    }
}
