package com.syit.cpc.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 发布内容响应 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublishContentResponse {

    /**
     * 内容 ID
     */
    private Long id;

    /**
     * 状态：pending-审核中
     */
    private String status;
}
