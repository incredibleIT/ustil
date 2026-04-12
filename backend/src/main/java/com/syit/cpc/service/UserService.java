package com.syit.cpc.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.syit.cpc.dto.request.ChangePasswordRequest;
import com.syit.cpc.dto.request.UpdateProfileRequest;
import com.syit.cpc.dto.request.UpdateUserRoleRequest;
import com.syit.cpc.dto.request.UpdateUserStatusRequest;
import com.syit.cpc.dto.response.MemberListResponse;
import com.syit.cpc.dto.response.UserProfileResponse;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 获取用户个人信息
     *
     * @param userId 用户ID
     * @return 用户个人信息
     */
    UserProfileResponse getUserProfile(Long userId);

    /**
     * 修改个人信息
     *
     * @param userId  用户ID
     * @param request 修改请求
     */
    void updateProfile(Long userId, UpdateProfileRequest request);

    /**
     * 修改密码
     *
     * @param userId  用户ID
     * @param request 修改密码请求
     */
    void changePassword(Long userId, ChangePasswordRequest request);

    /**
     * 分页查询成员列表（管理员）
     *
     * @param current 当前页码
     * @param size    每页大小
     * @param role    角色筛选（可选）
     * @param keyword 搜索关键词（可选）
     * @return 分页成员列表
     */
    MemberListResponse getMemberList(Long current, Long size, String role, String keyword);

    /**
     * 更新用户角色（管理员）
     *
     * @param userId  用户ID
     * @param request 更新角色请求
     */
    void updateUserRole(Long userId, UpdateUserRoleRequest request);

    /**
     * 更新用户状态（管理员）
     *
     * @param userId  用户ID
     * @param request 更新状态请求
     */
    void updateUserStatus(Long userId, UpdateUserStatusRequest request);
}
