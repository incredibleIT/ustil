package com.syit.cpc.service;

import com.syit.cpc.dto.request.CreateQuestionRequest;
import com.syit.cpc.dto.request.SubmitExamRequest;
import com.syit.cpc.dto.request.UpdateQuestionRequest;
import com.syit.cpc.dto.response.ExamResultResponse;
import com.syit.cpc.dto.response.PaperResponse;
import com.syit.cpc.dto.response.QuestionListResponse;
import com.syit.cpc.dto.response.QuestionResponse;

import java.util.Map;

/**
 * 题库服务接口
 */
public interface QuestionService {

    /**
     * 分页查询题目列表
     *
     * @param current 当前页码
     * @param size    每页大小
     * @param type    题型筛选（可选）
     * @param keyword 搜索关键词（可选）
     * @return 分页题目列表
     */
    QuestionListResponse getQuestionList(Long current, Long size, String type, String keyword);

    /**
     * 创建题目
     *
     * @param request 创建题目请求
     * @return 题目详情
     */
    QuestionResponse createQuestion(CreateQuestionRequest request);

    /**
     * 更新题目
     *
     * @param id      题目ID
     * @param request 更新题目请求
     * @return 题目详情
     */
    QuestionResponse updateQuestion(Long id, UpdateQuestionRequest request);

    /**
     * 删除题目（软删除）
     *
     * @param id 题目ID
     */
    void deleteQuestion(Long id);

    /**
     * 获取题目详情
     *
     * @param id 题目ID
     * @return 题目详情
     */
    QuestionResponse getQuestionDetail(Long id);

    /**
     * 随机生成试卷
     *
     * @param userId 用户ID（用于随机种子）
     * @return 试卷（不包含正确答案）
     */
    PaperResponse generatePaper(Long userId);

    /**
     * 获取题目统计信息
     *
     * @return 各题型数量统计
     */
    Map<String, Long> getQuestionStats();

    /**
     * 提交试卷并自动判卷
     *
     * @param userId  用户ID
     * @param request 提交试卷请求
     * @return 考试结果
     */
    ExamResultResponse submitExam(Long userId, SubmitExamRequest request);

    /**
     * 获取用户最新考试结果
     *
     * @param userId 用户ID
     * @return 考试结果
     */
    ExamResultResponse getLatestExamResult(Long userId);
}
