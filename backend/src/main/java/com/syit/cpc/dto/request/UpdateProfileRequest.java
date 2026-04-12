package com.syit.cpc.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 修改个人信息请求 DTO
 */
@Data
@Schema(description = "修改个人信息请求")
public class UpdateProfileRequest {

    @Schema(description = "用户名/昵称")
    @Size(min = 2, max = 20, message = "昵称长度必须在 2-20 个字符之间")
    private String username;

    @Schema(description = "个人简介")
    @Size(max = 200, message = "个人简介不能超过 200 个字符")
    private String bio;
}
