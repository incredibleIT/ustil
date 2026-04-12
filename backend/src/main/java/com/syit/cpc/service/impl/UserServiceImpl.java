package com.syit.cpc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.syit.cpc.common.constant.UserRole;
import com.syit.cpc.common.exception.BusinessException;
import com.syit.cpc.common.exception.ErrorCode;
import com.syit.cpc.dto.request.ChangePasswordRequest;
import com.syit.cpc.dto.request.UpdateProfileRequest;
import com.syit.cpc.dto.request.UpdateUserRoleRequest;
import com.syit.cpc.dto.request.UpdateUserStatusRequest;
import com.syit.cpc.dto.response.MemberListResponse;
import com.syit.cpc.dto.response.UserProfileResponse;
import com.syit.cpc.entity.User;
import com.syit.cpc.mapper.UserMapper;
import com.syit.cpc.service.UserService;
import com.syit.cpc.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordUtil passwordUtil;

    @Override
    public UserProfileResponse getUserProfile(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        UserProfileResponse response = new UserProfileResponse();
        response.setUserId(user.getId());
        response.setStudentId(user.getStudentId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setAvatar(user.getAvatar());
        response.setBio(user.getBio());
        response.setStatus(user.getStatus());
        response.setCreatedAt(user.getCreatedAt());
        
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 只更新非空字段
        boolean updated = false;
        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            user.setUsername(request.getUsername());
            updated = true;
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
            updated = true;
        }

        if (updated) {
            userMapper.updateById(user);
            log.info("用户信息已更新: userId={}", userId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 验证旧密码
        if (!passwordUtil.matchesPassword(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.OLD_PASSWORD_ERROR);
        }

        // 加密并更新新密码
        user.setPassword(passwordUtil.encodePassword(request.getNewPassword()));
        userMapper.updateById(user);
        
        log.info("用户密码已修改: userId={}", userId);
    }

    @Override
    public MemberListResponse getMemberList(Long current, Long size, String role, String keyword) {
        // 边界值保护
        current = Math.max(1, current);
        size = Math.max(1, Math.min(100, size));

        // 创建分页对象
        Page<User> page = new Page<>(current, size);

        // 构建查询条件
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        // 角色筛选
        if (StringUtils.hasText(role)) {
            wrapper.eq(User::getRole, role);
        }

        // 关键词搜索（学号或姓名）
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                .like(User::getStudentId, keyword)
                .or()
                .like(User::getUsername, keyword)
            );
        }

        // 按加入时间倒序
        wrapper.orderByDesc(User::getCreatedAt);

        // 执行分页查询
        Page<User> resultPage = userMapper.selectPage(page, wrapper);

        // 转换为 Response DTO
        MemberListResponse response = new MemberListResponse();
        response.setTotal(resultPage.getTotal());
        response.setCurrent(resultPage.getCurrent());
        response.setSize(resultPage.getSize());
        response.setPages(resultPage.getPages());

        List<MemberListResponse.MemberInfo> records = resultPage.getRecords().stream()
            .map(this::convertToMemberInfo)
            .collect(Collectors.toList());
        response.setRecords(records);

        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserRole(Long userId, UpdateUserRoleRequest request) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 校验角色值
        String newRole = request.getRole();
        if (!UserRole.isValid(newRole)) {
            throw new BusinessException(ErrorCode.INVALID_ROLE);
        }

        // 禁止修改自己的角色
        if (user.getRole().equals(UserRole.ROLE_ADMIN.getCode())) {
            throw new BusinessException(ErrorCode.INVALID_ROLE, "不能修改负责人的角色");
        }

        user.setRole(newRole);
        userMapper.updateById(user);
        
        log.info("用户角色已更新: userId={}, newRole={}", userId, newRole);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserStatus(Long userId, UpdateUserStatusRequest request) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 校验状态值
        Integer newStatus = request.getStatus();
        if (newStatus != 0 && newStatus != 1) {
            throw new BusinessException(ErrorCode.USER_STATUS_UPDATE_FAILED);
        }

        // 禁止禁用/启用自己的账号
        if (user.getRole().equals(UserRole.ROLE_ADMIN.getCode())) {
            throw new BusinessException(ErrorCode.USER_STATUS_UPDATE_FAILED, "不能修改负责人的账号状态");
        }

        user.setStatus(newStatus);
        userMapper.updateById(user);
        
        log.info("用户状态已更新: userId={}, newStatus={}", userId, newStatus);
    }

    /**
     * 将 User 实体转换为 MemberInfo
     */
    private MemberListResponse.MemberInfo convertToMemberInfo(User user) {
        MemberListResponse.MemberInfo info = new MemberListResponse.MemberInfo();
        info.setUserId(user.getId());
        info.setStudentId(user.getStudentId());
        info.setUsername(user.getUsername());
        info.setEmail(user.getEmail());
        info.setRole(user.getRole());
        info.setAvatar(user.getAvatar());
        info.setStatus(user.getStatus());
        info.setCreatedAt(user.getCreatedAt());
        return info;
    }
}
