package com.syit.cpc.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 密码匹配校验注解
 * 用于验证 newPassword 和 confirmPassword 是否一致
 */
@Documented
@Constraint(validatedBy = PasswordMatchValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordMatch {

    String message() default "两次输入的密码不一致";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * 新密码字段名
     */
    String password() default "newPassword";

    /**
     * 确认密码字段名
     */
    String confirmPassword() default "confirmPassword";
}
