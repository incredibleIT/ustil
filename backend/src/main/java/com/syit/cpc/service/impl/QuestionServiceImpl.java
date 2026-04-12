package com.syit.cpc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.syit.cpc.common.exception.BusinessException;
import com.syit.cpc.common.exception.ErrorCode;
import com.syit.cpc.dto.request.CreateQuestionRequest;
import com.syit.cpc.dto.request.SubmitExamRequest;
import com.syit.cpc.dto.request.UpdateQuestionRequest;
import com.syit.cpc.dto.response.ExamResultResponse;
import com.syit.cpc.dto.response.PaperResponse;
import com.syit.cpc.dto.response.QuestionListResponse;
import com.syit.cpc.dto.response.QuestionResponse;
import com.syit.cpc.entity.ExamRecord;
import com.syit.cpc.entity.PromotionApplication;
import com.syit.cpc.entity.PromotionStatus;
import com.syit.cpc.entity.Question;
import com.syit.cpc.mapper.ExamRecordMapper;
import com.syit.cpc.mapper.PromotionApplicationMapper;
import com.syit.cpc.mapper.QuestionMapper;
import com.syit.cpc.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 题库服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionMapper questionMapper;
    private final ExamRecordMapper examRecordMapper;
    private final PromotionApplicationMapper promotionApplicationMapper;

    @Override
    public QuestionListResponse getQuestionList(Long current, Long size, String type, String keyword) {
        // 边界值保护
        current = Math.max(1, current);
        size = Math.max(1, Math.min(100, size));

        // 创建分页对象
        Page<Question> page = new Page<>(current, size);

        // 构建查询条件
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();

        // 题型筛选
        if (StringUtils.hasText(type)) {
            wrapper.eq(Question::getQuestionType, type);
        }

        // 关键词搜索（题目内容）
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Question::getQuestionText, keyword);
        }

        // 按创建时间倒序
        wrapper.orderByDesc(Question::getCreatedAt);

        // 执行分页查询
        Page<Question> resultPage = questionMapper.selectPage(page, wrapper);

        // 转换为 Response DTO
        IPage<QuestionResponse> responsePage = resultPage.convert(this::convertToResponse);
        
        return QuestionListResponse.fromPage(responsePage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public QuestionResponse createQuestion(CreateQuestionRequest request) {
        // 验证题目数据
        validateQuestionData(request.getQuestionType(), request.getOptions(), request.getCorrectAnswer());

        // 查重：检查是否存在相同题干
        LambdaQueryWrapper<Question> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(Question::getQuestionText, request.getQuestionText());
        if (questionMapper.selectCount(checkWrapper) > 0) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "题目内容已存在，请勿重复添加");
        }

        // 创建题目实体
        Question question = new Question();
        BeanUtils.copyProperties(request, question);

        // 多选题答案排序，确保数据一致性
        if ("multiple_choice".equals(request.getQuestionType()) && request.getCorrectAnswer() != null) {
            char[] chars = request.getCorrectAnswer().toCharArray();
            Arrays.sort(chars);
            question.setCorrectAnswer(new String(chars));
        }

        // 插入数据库
        questionMapper.insert(question);
        log.info("题目已创建: id={}, type={}", question.getId(), question.getQuestionType());

        return convertToResponse(question);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public QuestionResponse updateQuestion(Long id, UpdateQuestionRequest request) {
        // 查询题目
        Question question = questionMapper.selectById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.QUESTION_NOT_FOUND);
        }

        // 查重：检查是否存在相同题干（排除当前题目）
        if (!question.getQuestionText().equals(request.getQuestionText())) {
            LambdaQueryWrapper<Question> checkWrapper = new LambdaQueryWrapper<>();
            checkWrapper.eq(Question::getQuestionText, request.getQuestionText());
            checkWrapper.ne(Question::getId, id);
            if (questionMapper.selectCount(checkWrapper) > 0) {
                throw new BusinessException(ErrorCode.BUSINESS_ERROR, "题目内容已存在，请勿使用重复题干");
            }
        }

        // 验证题目数据
        validateQuestionData(request.getQuestionType(), request.getOptions(), request.getCorrectAnswer());

        // 更新字段
        question.setQuestionText(request.getQuestionText());
        question.setQuestionType(request.getQuestionType());
        question.setOptions(request.getOptions());
        question.setScore(request.getScore());
        
        // 多选题答案排序，确保数据一致性
        if ("multiple_choice".equals(request.getQuestionType()) && request.getCorrectAnswer() != null) {
            char[] chars = request.getCorrectAnswer().toCharArray();
            Arrays.sort(chars);
            question.setCorrectAnswer(new String(chars));
        } else {
            question.setCorrectAnswer(request.getCorrectAnswer());
        }
        
        questionMapper.updateById(question);
        
        log.info("题目已更新: id={}", id);
        return convertToResponse(question);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteQuestion(Long id) {
        Question question = questionMapper.selectById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.QUESTION_NOT_FOUND);
        }

        // 软删除（MyBatis-Plus @TableLogic 会自动处理）
        questionMapper.deleteById(id);
        log.info("题目已删除: id={}", id);
    }

    @Override
    public QuestionResponse getQuestionDetail(Long id) {
        Question question = questionMapper.selectById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.QUESTION_NOT_FOUND);
        }

        return convertToResponse(question);
    }

    @Override
    public Map<String, Long> getQuestionStats() {
        // 统计各题型数量
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        List<Question> allQuestions = questionMapper.selectList(wrapper);
        
        if (allQuestions == null) {
            allQuestions = Collections.emptyList();
        }
        
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", (long) allQuestions.size());
        stats.put("single_choice", allQuestions.stream().filter(q -> "single_choice".equals(q.getQuestionType())).count());
        stats.put("multiple_choice", allQuestions.stream().filter(q -> "multiple_choice".equals(q.getQuestionType())).count());
        stats.put("true_false", allQuestions.stream().filter(q -> "true_false".equals(q.getQuestionType())).count());
        stats.put("short_answer", allQuestions.stream().filter(q -> "short_answer".equals(q.getQuestionType())).count());
        
        return stats;
    }

    @Override
    public PaperResponse generatePaper(Long userId) {
        // 抽取单选题 15 题
        List<Question> singleChoice = getRandomQuestionsByType("single_choice", 15, userId);
        // 抽取多选题 5 题
        List<Question> multipleChoice = getRandomQuestionsByType("multiple_choice", 5, userId);
        // 抽取判断题 10 题
        List<Question> trueFalse = getRandomQuestionsByType("true_false", 10, userId);

        // 检查题库是否足够
        if (singleChoice.size() < 15 || multipleChoice.size() < 5 || trueFalse.size() < 10) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_QUESTIONS);
        }

        // 合并所有题目
        List<Question> allQuestions = new ArrayList<>();
        allQuestions.addAll(singleChoice);
        allQuestions.addAll(multipleChoice);
        allQuestions.addAll(trueFalse);

        // 转换为试卷响应（不包含正确答案）
        List<PaperResponse.QuestionItem> questionItems = allQuestions.stream()
            .map(this::convertToPaperItem)
            .collect(Collectors.toList());

        // 计算总分
        int totalScore = allQuestions.stream()
            .mapToInt(Question::getScore)
            .sum();

        // 构建响应（使用UUID作为paperId，避免高并发重复）
        PaperResponse response = new PaperResponse();
        response.setPaperId(System.currentTimeMillis() ^ (long)(Math.random() * 1000000));
        response.setQuestions(questionItems);
        response.setTotalScore(totalScore);
        response.setDuration(60); // 60 分钟

        log.info("试卷已生成: userId={}, paperId={}, totalScore={}", userId, response.getPaperId(), totalScore);
        return response;
    }

    /**
     * 随机抽取指定类型的题目
     * 注意：当前实现适用于题库 < 1000 的场景
     * 题库更大时应使用数据库层面的随机查询：
     *   SELECT * FROM questions WHERE type = ? ORDER BY RAND() LIMIT ?
     */
    private List<Question> getRandomQuestionsByType(String questionType, int count, Long userId) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Question::getQuestionType, questionType);
        List<Question> questions = questionMapper.selectList(wrapper);

        if (questions.isEmpty()) {
            return Collections.emptyList();
        }

        // 基于用户ID和时间戳生成随机种子，确保不同用户获得不同题目
        long seed = userId.hashCode() ^ System.currentTimeMillis();
        Random random = new Random(seed);
        
        // 随机打乱
        Collections.shuffle(questions, random);
        
        // 返回指定数量
        return questions.subList(0, Math.min(count, questions.size()));
    }

    /**
     * 及格分数线
     */
    private static final int PASSING_SCORE_THRESHOLD = 60;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExamResultResponse submitExam(Long userId, SubmitExamRequest request) {
        log.info("提交试卷: userId={}, paperId={}, answerCount={}", userId, request.getPaperId(), request.getAnswers().size());

        // 0. 重复提交防护
        LambdaQueryWrapper<ExamRecord> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(ExamRecord::getUserId, userId);
        checkWrapper.eq(ExamRecord::getPaperId, request.getPaperId());
        long submittedCount = examRecordMapper.selectCount(checkWrapper);
        if (submittedCount > 0) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "该试卷已提交，不可重复提交");
        }

        // 0.1 验证开始时间（不能超过当前时间）
        if (request.getStartTime().isAfter(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "考试开始时间不能晚于当前时间");
        }

        // 1. 获取所有题目ID
        List<Long> questionIds = request.getAnswers().stream()
            .map(SubmitExamRequest.AnswerItem::getQuestionId)
            .collect(Collectors.toList());

        // 2. 批量查询题目
        if (questionIds.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "答案列表不能为空");
        }
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Question::getId, questionIds);
        List<Question> questions = questionMapper.selectList(wrapper);

        if (questions.size() != questionIds.size()) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "部分题目不存在");
        }

        // 3. 构建答案映射
        Map<Long, String> userAnswers = request.getAnswers().stream()
            .collect(Collectors.toMap(SubmitExamRequest.AnswerItem::getQuestionId, SubmitExamRequest.AnswerItem::getAnswer));

        // 4. 判卷
        List<ExamResultResponse.ExamDetailItem> details = new ArrayList<>();
        int totalScore = 0;
        int singleChoiceScore = 0;
        int multipleChoiceScore = 0;
        int trueFalseScore = 0;
        int maxScore = 0;

        // 构建用户答案JSON（存储到数据库）
        Map<String, String> answersJson = new LinkedHashMap<>();

        for (Question question : questions) {
            String userAnswer = userAnswers.get(question.getId());
            boolean isCorrect = checkAnswer(userAnswer, question.getCorrectAnswer(), question.getQuestionType());
            int questionScore = isCorrect ? question.getScore() : 0;
            totalScore += questionScore;
            maxScore += question.getScore();

            // 按题型累加得分
            switch (question.getQuestionType()) {
                case "single_choice":
                    singleChoiceScore += questionScore;
                    break;
                case "multiple_choice":
                    multipleChoiceScore += questionScore;
                    break;
                case "true_false":
                    trueFalseScore += questionScore;
                    break;
            }

            // 构建详情
            ExamResultResponse.ExamDetailItem detail = new ExamResultResponse.ExamDetailItem();
            detail.setQuestionId(question.getId());
            detail.setQuestionText(question.getQuestionText());
            detail.setQuestionType(question.getQuestionType());
            detail.setUserAnswer(userAnswer != null ? userAnswer : "未作答");
            detail.setCorrectAnswer(question.getCorrectAnswer());
            detail.setCorrect(isCorrect);
            detail.setScore(isCorrect ? question.getScore() : 0);
            details.add(detail);

            // 构建答案JSON
            answersJson.put(String.valueOf(question.getId()), userAnswer != null ? userAnswer : "");
        }

        // 5. 计算考试次数（取最大 attempt_count + 1）
        LambdaQueryWrapper<ExamRecord> countWrapper = new LambdaQueryWrapper<>();
        countWrapper.eq(ExamRecord::getUserId, userId);
        countWrapper.orderByDesc(ExamRecord::getAttemptCount);
        countWrapper.last("LIMIT 1");
        ExamRecord lastRecord = examRecordMapper.selectOne(countWrapper);
        int attemptCount = (lastRecord != null ? lastRecord.getAttemptCount() : 0) + 1;

        // 6. 创建考试记录
        ExamRecord examRecord = new ExamRecord();
        examRecord.setUserId(userId);
        examRecord.setPaperId(request.getPaperId());
        examRecord.setAnswers(answersJson);
        examRecord.setScore(totalScore);
        examRecord.setMaxScore(maxScore);
        examRecord.setPassed(totalScore >= PASSING_SCORE_THRESHOLD);
        examRecord.setStartedAt(request.getStartTime());
        examRecord.setCompletedAt(LocalDateTime.now());
        examRecord.setDuration((int) java.time.Duration.between(request.getStartTime(), examRecord.getCompletedAt()).toMinutes());
        examRecord.setAttemptCount(attemptCount);
        examRecordMapper.insert(examRecord);

        // 7. 更新转正申请状态
        LambdaQueryWrapper<PromotionApplication> promoWrapper = new LambdaQueryWrapper<>();
        promoWrapper.eq(PromotionApplication::getUserId, userId);
        promoWrapper.eq(PromotionApplication::getDeleted, 0);
        PromotionApplication promotion = promotionApplicationMapper.selectOne(promoWrapper);
        
        if (promotion != null) {
            promotion.setExamScore(totalScore);
            if (totalScore >= PASSING_SCORE_THRESHOLD) {
                promotion.setStatus(PromotionStatus.PENDING_PROJECT.getCode());
            } else {
                promotion.setStatus(PromotionStatus.EXAM_FAILED.getCode());
            }
            promotionApplicationMapper.updateById(promotion);
            log.info("更新转正申请状态: userId={}, examScore={}, status={}", userId, totalScore, promotion.getStatus());
        }

        // 8. 构建响应
        ExamResultResponse response = new ExamResultResponse();
        response.setExamRecordId(examRecord.getId());
        response.setScore(totalScore);
        response.setMaxScore(maxScore);
        response.setPassed(totalScore >= PASSING_SCORE_THRESHOLD);
        response.setSingleChoiceScore(singleChoiceScore);
        response.setMultipleChoiceScore(multipleChoiceScore);
        response.setTrueFalseScore(trueFalseScore);
        response.setAttemptCount(attemptCount);
        response.setStartedAt(examRecord.getStartedAt());
        response.setCompletedAt(examRecord.getCompletedAt());
        response.setDuration(examRecord.getDuration());
        response.setDetails(details);

        log.info("试卷提交完成: userId={}, score={}, passed={}, duration={}分钟", userId, totalScore, totalScore >= PASSING_SCORE_THRESHOLD, examRecord.getDuration());
        return response;
    }

    @Override
    public ExamResultResponse getLatestExamResult(Long userId) {
        LambdaQueryWrapper<ExamRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamRecord::getUserId, userId);
        wrapper.orderByDesc(ExamRecord::getCreatedAt);
        wrapper.last("LIMIT 1");
        
        ExamRecord examRecord = examRecordMapper.selectOne(wrapper);
        if (examRecord == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "暂无考试记录");
        }

        // 重新计算详情和各题型得分
        Map<String, String> userAnswers = examRecord.getAnswers();
        List<ExamResultResponse.ExamDetailItem> details = Collections.emptyList();
        int singleChoiceScore = 0;
        int multipleChoiceScore = 0;
        int trueFalseScore = 0;

        if (userAnswers != null && !userAnswers.isEmpty()) {
            // 获取所有题目ID
            List<Long> questionIds = userAnswers.keySet().stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());

            // 批量查询题目
            LambdaQueryWrapper<Question> qWrapper = new LambdaQueryWrapper<>();
            qWrapper.in(Question::getId, questionIds);
            List<Question> questions = questionMapper.selectList(qWrapper);

            // 重新判卷
            details = new ArrayList<>();
            for (Question question : questions) {
                String userAnswer = userAnswers.get(String.valueOf(question.getId()));
                boolean isCorrect = checkAnswer(userAnswer, question.getCorrectAnswer(), question.getQuestionType());
                int questionScore = isCorrect ? question.getScore() : 0;

                // 按题型累加得分
                switch (question.getQuestionType()) {
                    case "single_choice":
                        singleChoiceScore += questionScore;
                        break;
                    case "multiple_choice":
                        multipleChoiceScore += questionScore;
                        break;
                    case "true_false":
                        trueFalseScore += questionScore;
                        break;
                }

                // 构建详情
                ExamResultResponse.ExamDetailItem detail = new ExamResultResponse.ExamDetailItem();
                detail.setQuestionId(question.getId());
                detail.setQuestionText(question.getQuestionText());
                detail.setQuestionType(question.getQuestionType());
                detail.setUserAnswer(userAnswer != null && !userAnswer.isEmpty() ? userAnswer : "未作答");
                detail.setCorrectAnswer(question.getCorrectAnswer());
                detail.setCorrect(isCorrect);
                detail.setScore(questionScore);
                details.add(detail);
            }
        }

        ExamResultResponse response = new ExamResultResponse();
        response.setExamRecordId(examRecord.getId());
        response.setScore(examRecord.getScore());
        response.setMaxScore(examRecord.getMaxScore());
        response.setPassed(examRecord.getPassed());
        response.setSingleChoiceScore(singleChoiceScore);
        response.setMultipleChoiceScore(multipleChoiceScore);
        response.setTrueFalseScore(trueFalseScore);
        response.setAttemptCount(examRecord.getAttemptCount());
        response.setStartedAt(examRecord.getStartedAt());
        response.setCompletedAt(examRecord.getCompletedAt());
        response.setDuration(examRecord.getDuration());
        response.setDetails(details);

        return response;
    }

    /**
     * 检查答案是否正确
     */
    private boolean checkAnswer(String userAnswer, String correctAnswer, String questionType) {
        // 未作答或题目数据异常直接判定为错误
        if (userAnswer == null || userAnswer.isEmpty()) {
            return false;
        }
        if (correctAnswer == null || correctAnswer.isEmpty()) {
            return false;
        }

        // 多选题：答案已排序存储，对用户答案排序后比较
        if ("multiple_choice".equals(questionType)) {
            // 多选题至少需要 2 个选项
            if (userAnswer.length() < 2) {
                return false;
            }
            char[] chars = userAnswer.toCharArray();
            Arrays.sort(chars);
            String sortedUserAnswer = new String(chars);
            return sortedUserAnswer.equals(correctAnswer);
        }

        // 单选和判断：精确匹配
        return userAnswer.equals(correctAnswer);
    }

    /**
     * 验证题目数据
     */
    private void validateQuestionData(String questionType, List<String> options, String correctAnswer) {
        switch (questionType) {
            case "single_choice":
                // 单选题：4个选项，答案为单个字母
                if (CollectionUtils.isEmpty(options) || options.size() != 4) {
                    throw new BusinessException(ErrorCode.OPTIONS_COUNT_INVALID, "单选题必须有4个选项");
                }
                if (correctAnswer == null || !correctAnswer.matches("^[A-D]$")) {
                    throw new BusinessException(ErrorCode.ANSWER_FORMAT_INVALID, "单选题答案必须为 A/B/C/D 之一");
                }
                break;

            case "multiple_choice":
                // 多选题：4-6个选项，答案为2-4个字母
                if (CollectionUtils.isEmpty(options) || options.size() < 4 || options.size() > 6) {
                    throw new BusinessException(ErrorCode.OPTIONS_COUNT_INVALID, "多选题必须有4-6个选项");
                }
                if (correctAnswer == null || !correctAnswer.matches("^[A-F]{2,4}$")) {
                    throw new BusinessException(ErrorCode.ANSWER_FORMAT_INVALID, "多选题答案必须为2-4个大写字母");
                }
                break;

            case "true_false":
                // 判断题：答案为"对"或"错"
                if (correctAnswer == null || (!correctAnswer.equals("对") && !correctAnswer.equals("错"))) {
                    throw new BusinessException(ErrorCode.ANSWER_FORMAT_INVALID, "判断题答案必须为'对'或'错'");
                }
                break;

            case "short_answer":
                // 简答题：无选项，无标准答案（人工评分）
                break;

            default:
                throw new BusinessException(ErrorCode.QUESTION_TYPE_INVALID);
        }
    }

    /**
     * 将 Question 实体转换为 QuestionResponse
     */
    private QuestionResponse convertToResponse(Question question) {
        QuestionResponse response = new QuestionResponse();
        response.setId(question.getId());
        response.setQuestionText(question.getQuestionText());
        response.setQuestionType(question.getQuestionType());
        response.setOptions(question.getOptions());
        response.setCorrectAnswer(question.getCorrectAnswer());
        response.setScore(question.getScore());
        response.setCreatedAt(question.getCreatedAt());
        response.setUpdatedAt(question.getUpdatedAt());
        return response;
    }

    /**
     * 将 Question 实体转换为 PaperResponse.QuestionItem（不包含答案）
     */
    private PaperResponse.QuestionItem convertToPaperItem(Question question) {
        PaperResponse.QuestionItem item = new PaperResponse.QuestionItem();
        item.setId(question.getId());
        item.setQuestionText(question.getQuestionText());
        item.setQuestionType(question.getQuestionType());
        item.setOptions(question.getOptions());
        item.setScore(question.getScore());
        return item;
    }
}

