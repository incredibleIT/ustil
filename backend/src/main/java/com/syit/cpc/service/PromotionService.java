package com.syit.cpc.service;

import com.syit.cpc.dto.request.ReviewProjectRequest;
import com.syit.cpc.dto.request.SubmitProjectRequest;
import com.syit.cpc.dto.response.PromotionInfoResponse;
import com.syit.cpc.dto.response.PromotionReviewListResponse;
import com.syit.cpc.dto.response.PromotionStatusResponse;

/**
 * 转正申请服务接口
 */
public interface PromotionService {

    /**
     * 获取转正流程说明
     *
     * @return 转正流程说明
     */
    PromotionInfoResponse getPromotionInfo();

    /**
     * 获取当前用户转正状态
     *
     * @param userId 用户ID
     * @return 转正状态
     */
    PromotionStatusResponse getPromotionStatus(Long userId);

    /**
     * 提交转正申请
     *
     * @param userId 用户ID
     * @return 申请结果
     */
    PromotionStatusResponse submitPromotion(Long userId);

    /**
     * 提交项目（预备成员）
     *
     * @param userId 用户ID
     * @param request 项目提交请求
     * @return 提交结果
     */
    PromotionStatusResponse submitProject(Long userId, SubmitProjectRequest request);

    /**
     * 评审项目（负责人）
     *
     * @param adminId 负责人ID
     * @param request 评审请求
     * @return 评审结果
     */
    PromotionStatusResponse reviewProject(Long adminId, ReviewProjectRequest request);

    /**
     * 获取待评审列表（负责人）
     *
     * @param page 页码
     * @param size 每页大小
     * @param status 状态筛选（可选）
     * @return 分页评审列表
     */
    PromotionReviewListResponse getPendingReviews(int page, int size, String status);
}
