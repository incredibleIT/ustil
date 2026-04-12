package com.syit.cpc.service;

import com.syit.cpc.common.response.ApiResponse;
import com.syit.cpc.dto.request.PublishContentRequest;
import com.syit.cpc.dto.request.UpdateContentRequest;
import com.syit.cpc.dto.response.ContentDetailResponse;
import com.syit.cpc.dto.response.PublishContentResponse;
import com.syit.cpc.dto.response.UserContentResponse;

import java.util.List;

/**
 * 内容服务接口
 */
public interface ContentService {

    /**
     * 发布内容
     *
     * @param request  发布请求
     * @param authorId 作者 ID
     * @return 发布结果
     */
    ApiResponse<PublishContentResponse> publishContent(PublishContentRequest request, Long authorId);

    /**
     * 获取用户的内容列表
     *
     * @param userId 用户 ID
     * @return 内容列表
     */
    ApiResponse<List<UserContentResponse>> getUserContents(Long userId);

    /**
     * 获取内容详情
     *
     * @param contentId 内容 ID
     * @param userId    当前用户 ID
     * @return 内容详情
     */
    ApiResponse<ContentDetailResponse> getContentById(Long contentId, Long userId);

    /**
     * 更新内容
     *
     * @param contentId 内容 ID
     * @param request   更新请求
     * @param userId    当前用户 ID
     * @return 更新结果
     */
    ApiResponse<PublishContentResponse> updateContent(Long contentId, UpdateContentRequest request, Long userId);

    /**
     * 删除内容
     *
     * @param contentId 内容 ID
     * @param userId    当前用户 ID
     * @param reason    删除原因（管理员可选）
     * @return 删除结果
     */
    ApiResponse<Void> deleteContent(Long contentId, Long userId, String reason);

    /**
     * 获取所有内容列表（仅管理员）
     *
     * @return 所有内容列表
     */
    ApiResponse<List<UserContentResponse>> getAllContents();
}
