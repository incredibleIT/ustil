package com.syit.cpc.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 成员列表响应 DTO（分页）
 */
@Data
@Schema(description = "成员列表响应")
public class MemberListResponse {

    @Schema(description = "成员列表")
    private List<MemberInfo> records;

    @Schema(description = "总记录数")
    private Long total;

    @Schema(description = "当前页码")
    private Long current;

    @Schema(description = "每页大小")
    private Long size;

    @Schema(description = "总页数")
    private Long pages;

    @Data
    @Schema(description = "成员信息")
    public static class MemberInfo {
        @Schema(description = "用户ID")
        private Long userId;

        @Schema(description = "学号")
        private String studentId;

        @Schema(description = "用户名/昵称")
        private String username;

        @Schema(description = "邮箱")
        private String email;

        @Schema(description = "角色")
        private String role;

        @Schema(description = "头像URL")
        private String avatar;

        @Schema(description = "账号状态: 0-禁用, 1-启用")
        private Integer status;

        @Schema(description = "加入时间")
        private LocalDateTime createdAt;
    }
}
