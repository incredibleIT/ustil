package com.syit.cpc.dto.response;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.util.List;

/**
 * 题目分页列表响应 DTO
 */
@Data
public class QuestionListResponse {

    private Long total;
    private Long pages;
    private Long current;
    private Long size;
    private List<QuestionResponse> records;

    /**
     * 从 MyBatis-Plus Page 对象转换
     */
    public static QuestionListResponse fromPage(IPage<QuestionResponse> page) {
        QuestionListResponse response = new QuestionListResponse();
        response.setTotal(page.getTotal());
        response.setPages(page.getPages());
        response.setCurrent(page.getCurrent());
        response.setSize(page.getSize());
        response.setRecords(page.getRecords());
        return response;
    }
}
