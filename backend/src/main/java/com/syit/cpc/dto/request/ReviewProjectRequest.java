package com.syit.cpc.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 评审项目请求
 */
@Data
public class ReviewProjectRequest {

    /**
     * 转正申请ID
     */
    @NotNull(message = "申请ID不能为空")
    private Long applicationId;

    /**
     * 项目评分（0-100分，必填）
     */
    @NotNull(message = "项目评分不能为空")
    @Min(value = 0, message = "项目评分不能低于0分")
    @Max(value = 100, message = "项目评分不能超过100分")
    private Integer projectScore;

    /**
     * 评审意见（可选，最多500字符）
     */
    @Size(max = 500, message = "评审意见不能超过500个字符")
    private String reviewComment;
}
