package com.syit.cpc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.syit.cpc.common.exception.BusinessException;
import com.syit.cpc.common.exception.ErrorCode;
import com.syit.cpc.dto.request.ReviewProjectRequest;
import com.syit.cpc.dto.request.SubmitProjectRequest;
import com.syit.cpc.dto.response.PromotionInfoResponse;
import com.syit.cpc.dto.response.PromotionReviewListResponse;
import com.syit.cpc.dto.response.PromotionStatusResponse;
import com.syit.cpc.entity.PromotionApplication;
import com.syit.cpc.entity.PromotionStatus;
import com.syit.cpc.entity.User;
import com.syit.cpc.mapper.PromotionApplicationMapper;
import com.syit.cpc.mapper.UserMapper;
import com.syit.cpc.service.NotificationService;
import com.syit.cpc.service.PromotionService;
import com.syit.cpc.service.UserService;
import com.syit.cpc.common.constant.UserRole;
import com.syit.cpc.dto.request.UpdateUserRoleRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

/**
 * 转正申请服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionApplicationMapper promotionApplicationMapper;
    private final UserMapper userMapper;
    private final NotificationService notificationService;
    private final UserService userService;

    @Override
    public PromotionInfoResponse getPromotionInfo() {
        PromotionInfoResponse response = new PromotionInfoResponse();
        response.setTitle("转正考核流程");
        response.setDescription("预备成员需完成答题和项目评审，总分达到70分即可成为正式成员");

        // 步骤列表
        PromotionInfoResponse.PromotionStep step1 = new PromotionInfoResponse.PromotionStep();
        step1.setStep(1);
        step1.setTitle("提交申请");
        step1.setDescription("确认参加转正考核");
        step1.setEstimatedTime("即时");

        PromotionInfoResponse.PromotionStep step2 = new PromotionInfoResponse.PromotionStep();
        step2.setStep(2);
        step2.setTitle("在线答题");
        step2.setDescription("完成题库抽题考试");
        step2.setEstimatedTime("60分钟");

        PromotionInfoResponse.PromotionStep step3 = new PromotionInfoResponse.PromotionStep();
        step3.setStep(3);
        step3.setTitle("项目提交");
        step3.setDescription("提交项目作品和说明");
        step3.setEstimatedTime("自行安排");

        PromotionInfoResponse.PromotionStep step4 = new PromotionInfoResponse.PromotionStep();
        step4.setStep(4);
        step4.setTitle("项目评审");
        step4.setDescription("负责人评审项目");
        step4.setEstimatedTime("3-5天");

        PromotionInfoResponse.PromotionStep step5 = new PromotionInfoResponse.PromotionStep();
        step5.setStep(5);
        step5.setTitle("结果通知");
        step5.setDescription("查看转正结果");
        step5.setEstimatedTime("即时");

        response.setSteps(Arrays.asList(step1, step2, step3, step4, step5));

        // 评分规则
        PromotionInfoResponse.ScoringRules scoringRules = new PromotionInfoResponse.ScoringRules();
        scoringRules.setExamWeight(0.6);
        scoringRules.setProjectWeight(0.4);
        scoringRules.setPassingScore(70);
        response.setScoringRules(scoringRules);

        return response;
    }

    @Override
    public PromotionStatusResponse getPromotionStatus(Long userId) {
        PromotionStatusResponse response = new PromotionStatusResponse();

        // 查询用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            log.error("认证用户不存在: userId={}", userId);
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 计算注册天数（按自然日计算，不是完整24小时）
        LocalDate registrationDate = user.getCreatedAt().toLocalDate();
        long registrationDays = ChronoUnit.DAYS.between(registrationDate, LocalDate.now());
        response.setRegistrationDays(registrationDays);

        // 查询最新的转正申请记录
        LambdaQueryWrapper<PromotionApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PromotionApplication::getUserId, userId)
               .orderByDesc(PromotionApplication::getCreatedAt)
               .last("LIMIT 1");
        PromotionApplication application = promotionApplicationMapper.selectOne(wrapper);

        if (application == null) {
            // 未申请过
            response.setHasApplication(false);
            // 检查是否可以申请（注册满30天）
            boolean canApply = registrationDays >= 30;
            response.setCanApply(canApply);
        } else {
            // 已有申请
            response.setHasApplication(true);
            
            // 填充申请信息
            PromotionStatusResponse.ApplicationInfo appInfo = new PromotionStatusResponse.ApplicationInfo();
            appInfo.setId(application.getId());
            appInfo.setStatus(application.getStatus());
            appInfo.setCreatedAt(application.getCreatedAt());
            appInfo.setExamScore(application.getExamScore());
            appInfo.setProjectScore(application.getProjectScore());
            appInfo.setTotalScore(application.getTotalScore());
            appInfo.setReviewComment(application.getReviewComment());
            response.setApplication(appInfo);

            // 检查是否可以重新申请（申请被拒绝且注册满30天）
            boolean canApply = PromotionStatus.REJECTED.getCode().equals(application.getStatus()) && registrationDays >= 30;
            response.setCanApply(canApply);
        }

        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PromotionStatusResponse submitPromotion(Long userId) {
        // 1. 检查用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            log.error("认证用户不存在: userId={}", userId);
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 2. 检查注册时间是否满30天
        LocalDate registrationDate = user.getCreatedAt().toLocalDate();
        long registrationDays = ChronoUnit.DAYS.between(registrationDate, LocalDate.now());
        if (registrationDays < 30) {
            long daysRemaining = 30 - registrationDays;
            throw new BusinessException(ErrorCode.REGISTRATION_TOO_RECENT,
                String.format("注册时间不足30天，还需等待%d天", daysRemaining));
        }

        // 3. 检查是否有未完成的转正申请
        LambdaQueryWrapper<PromotionApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PromotionApplication::getUserId, userId)
               .notIn(PromotionApplication::getStatus, 
                   PromotionStatus.APPROVED.getCode(), 
                   PromotionStatus.REJECTED.getCode());
        long count = promotionApplicationMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PROMOTION_APPLICATION_EXISTS);
        }

        // 4. 创建转正申请记录
        PromotionApplication application = new PromotionApplication();
        application.setUserId(userId);
        application.setStatus(PromotionStatus.PENDING_EXAM.getCode()); // 初始状态：待答题
        promotionApplicationMapper.insert(application);

        log.info("用户提交转正申请: userId={}, applicationId={}", userId, application.getId());

        // 5. 返回申请结果
        PromotionStatusResponse response = new PromotionStatusResponse();
        response.setHasApplication(true);
        response.setCanApply(false);
        response.setRegistrationDays(registrationDays);

        PromotionStatusResponse.ApplicationInfo appInfo = new PromotionStatusResponse.ApplicationInfo();
        appInfo.setId(application.getId());
        appInfo.setStatus(application.getStatus());
        appInfo.setCreatedAt(application.getCreatedAt());
        response.setApplication(appInfo);

        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PromotionStatusResponse submitProject(Long userId, SubmitProjectRequest request) {
        // 1. 查询用户的转正申请（使用行级锁防止并发重复提交）
        PromotionApplication application = promotionApplicationMapper.selectLatestWithLock(userId);

        if (application == null) {
            throw new BusinessException(ErrorCode.PROMOTION_APPLICATION_NOT_FOUND);
        }

        // 2. 验证状态是否为 pending_project
        if (!PromotionStatus.PENDING_PROJECT.getCode().equals(application.getStatus())) {
            if (PromotionStatus.PROJECT_REVIEWING.getCode().equals(application.getStatus())) {
                throw new BusinessException(ErrorCode.PROJECT_ALREADY_SUBMITTED);
            }
            throw new BusinessException(ErrorCode.INVALID_PROJECT_SUBMISSION_STATUS);
        }

        // 3. 更新项目信息
        application.setProjectName(request.getProjectName());
        application.setProjectDescription(request.getProjectDescription());
        application.setProjectUrl(request.getProjectUrl());
        application.setStatus(PromotionStatus.PROJECT_REVIEWING.getCode());
        promotionApplicationMapper.updateById(application);

        log.info("用户提交项目: userId={}, applicationId={}, projectName={}", 
            userId, application.getId(), request.getProjectName());

        // 4. 返回结果
        return buildPromotionStatusResponse(userId, application);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PromotionStatusResponse reviewProject(Long adminId, ReviewProjectRequest request) {
        // 1. 查询转正申请
        PromotionApplication application = promotionApplicationMapper.selectById(request.getApplicationId());
        if (application == null) {
            throw new BusinessException(ErrorCode.PROMOTION_APPLICATION_NOT_FOUND);
        }

        // 2. 验证状态是否为 project_reviewing
        if (!PromotionStatus.PROJECT_REVIEWING.getCode().equals(application.getStatus())) {
            throw new BusinessException(ErrorCode.PROJECT_NOT_PENDING_REVIEW);
        }

        // 3. 检查是否有考试分数
        if (application.getExamScore() == null) {
            throw new BusinessException(ErrorCode.PROMOTION_STATUS_INVALID, "考试分数不存在");
        }

        // 4. 计算总分：examScore × 0.6 + projectScore × 0.4（使用 double 提高精度）
        int examScore = application.getExamScore();
        int projectScore = request.getProjectScore();
        int totalScore = (int) Math.round(examScore * 0.6 + projectScore * 0.4);

        // 5. 更新申请记录
        application.setProjectScore(projectScore);
        application.setExamScore(examScore);
        application.setTotalScore(totalScore);
        application.setReviewedBy(adminId);
        application.setReviewComment(request.getReviewComment());

        // 6. 根据总分更新状态
        if (totalScore >= 70) {
            application.setStatus(PromotionStatus.APPROVED.getCode());
            
            // 6.1 更新用户角色为正式成员
            UpdateUserRoleRequest roleRequest = new UpdateUserRoleRequest();
            roleRequest.setRole(UserRole.ROLE_MEMBER.getCode());
            userService.updateUserRole(application.getUserId(), roleRequest);
            
            // 6.2 发送通过通知
            notificationService.sendPromotionApprovedNotification(
                application.getUserId(),
                application.getId(),
                examScore,
                projectScore,
                totalScore,
                request.getReviewComment()
            );
            
            log.info("用户转正: userId={}, applicationId={}, totalScore={}",
                application.getUserId(), application.getId(), totalScore);
        } else {
            application.setStatus(PromotionStatus.REJECTED.getCode());
            
            // 6.3 发送拒绝通知
            notificationService.sendPromotionRejectedNotification(
                application.getUserId(),
                application.getId(),
                examScore,
                projectScore,
                totalScore,
                request.getReviewComment()
            );
            
            log.info("转正拒绝: userId={}, applicationId={}, totalScore={}",
                application.getUserId(), application.getId(), totalScore);
        }
        
        // 6.4 设置评审完成时间
        application.setReviewedAt(LocalDateTime.now());

        promotionApplicationMapper.updateById(application);

        log.info("负责人评审项目: adminId={}, applicationId={}, examScore={}, projectScore={}, totalScore={}, status={}",
            adminId, application.getId(), examScore, projectScore, totalScore, application.getStatus());

        // 7. 返回结果
        return buildPromotionStatusResponse(application.getUserId(), application);
    }

    @Override
    public PromotionReviewListResponse getPendingReviews(int page, int size, String status) {
        // 1. 构建查询条件
        LambdaQueryWrapper<PromotionApplication> wrapper = new LambdaQueryWrapper<>();
        
        // 只查询待评审和评审中的记录
        if (status != null && !status.isEmpty()) {
            wrapper.eq(PromotionApplication::getStatus, status);
        } else {
            wrapper.in(PromotionApplication::getStatus, 
                PromotionStatus.PENDING_PROJECT.getCode(), 
                PromotionStatus.PROJECT_REVIEWING.getCode());
        }
        
        wrapper.orderByDesc(PromotionApplication::getCreatedAt);

        // 2. 分页查询
        Page<PromotionApplication> pageParam = new Page<>(page, size);
        Page<PromotionApplication> resultPage = promotionApplicationMapper.selectPage(pageParam, wrapper);

        // 3. 批量查询用户信息（解决 N+1 查询问题）
        List<Long> userIds = resultPage.getRecords().stream()
            .map(PromotionApplication::getUserId)
            .distinct()
            .toList();
        
        java.util.Map<Long, User> userMap = new java.util.HashMap<>();
        if (!userIds.isEmpty()) {
            List<User> users = userMapper.selectBatchIds(userIds);
            userMap = users.stream()
                .collect(java.util.stream.Collectors.toMap(User::getId, u -> u));
        }

        // 4. 转换为响应对象
        PromotionReviewListResponse response = new PromotionReviewListResponse();
        response.setTotal(resultPage.getTotal());
        response.setPages(resultPage.getPages());
        response.setCurrent(resultPage.getCurrent());
        response.setSize(resultPage.getSize());

        final java.util.Map<Long, User> finalUserMap = userMap;
        List<PromotionReviewListResponse.ReviewItem> records = resultPage.getRecords().stream()
            .map(app -> {
                PromotionReviewListResponse.ReviewItem item = new PromotionReviewListResponse.ReviewItem();
                item.setApplicationId(app.getId());
                item.setProjectName(app.getProjectName());
                item.setProjectDescription(app.getProjectDescription());
                item.setProjectUrl(app.getProjectUrl());
                item.setExamScore(app.getExamScore());
                item.setSubmittedAt(app.getUpdatedAt() != null ? app.getUpdatedAt() : app.getCreatedAt());
                item.setStatus(app.getStatus());

                // 从缓存的用户Map中获取申请人信息
                User user = finalUserMap.get(app.getUserId());
                if (user != null) {
                    item.setApplicantName(user.getUsername());
                } else {
                    // 用户被删除时的容错处理
                    item.setApplicantName("未知用户");
                }

                return item;
            })
            .toList();

        response.setRecords(records);
        return response;
    }

    /**
     * 构建 PromotionStatusResponse
     */
    private PromotionStatusResponse buildPromotionStatusResponse(Long userId, PromotionApplication application) {
        PromotionStatusResponse response = new PromotionStatusResponse();
        response.setHasApplication(true);
        response.setCanApply(false);

        // 计算注册天数
        User user = userMapper.selectById(userId);
        if (user != null) {
            LocalDate registrationDate = user.getCreatedAt().toLocalDate();
            long registrationDays = ChronoUnit.DAYS.between(registrationDate, LocalDate.now());
            response.setRegistrationDays(registrationDays);
        }

        // 填充申请信息
        PromotionStatusResponse.ApplicationInfo appInfo = new PromotionStatusResponse.ApplicationInfo();
        appInfo.setId(application.getId());
        appInfo.setStatus(application.getStatus());
        appInfo.setCreatedAt(application.getCreatedAt());
        appInfo.setExamScore(application.getExamScore());
        appInfo.setProjectScore(application.getProjectScore());
        appInfo.setTotalScore(application.getTotalScore());
        appInfo.setReviewComment(application.getReviewComment());
        response.setApplication(appInfo);

        return response;
    }
}
