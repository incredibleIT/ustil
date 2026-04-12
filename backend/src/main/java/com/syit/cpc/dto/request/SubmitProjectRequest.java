package com.syit.cpc.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 提交项目请求
 */
@Data
public class SubmitProjectRequest {

    /**
     * 项目名称（必填，最多200字符）
     */
    @NotBlank(message = "项目名称不能为空")
    @Size(max = 200, message = "项目名称不能超过200个字符")
    private String projectName;

    /**
     * 项目描述（必填，最多2000字符）
     */
    @NotBlank(message = "项目描述不能为空")
    @Size(max = 2000, message = "项目描述不能超过2000个字符")
    private String projectDescription;

    /**
     * 项目链接（可选，最多500字符，需符合URL格式）
     * 支持：http://、https://、localhost、IP地址
     */
    @Size(max = 500, message = "项目链接不能超过500个字符")
    @Pattern(regexp = "^$|^(https?://)(localhost|[\\w\\-]+(\\.[\\w\\-]+)+|\\d{1,3}(\\.\\d{1,3}){3})(:[\\d]+)?([/?#].*)?$", message = "项目链接格式不正确，需以 http:// 或 https:// 开头")
    private String projectUrl;
}
