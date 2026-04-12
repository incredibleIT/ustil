package com.syit.cpc.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

/**
 * 密码匹配校验器
 */
@Slf4j
public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, Object> {

    private String passwordField;
    private String confirmPasswordField;

    @Override
    public void initialize(PasswordMatch constraintAnnotation) {
        this.passwordField = constraintAnnotation.password();
        this.confirmPasswordField = constraintAnnotation.confirmPassword();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        try {
            // 获取密码字段值
            Field passwordFld = value.getClass().getDeclaredField(passwordField);
            passwordFld.setAccessible(true);
            Object password = passwordFld.get(value);

            // 获取确认密码字段值
            Field confirmPwdFld = value.getClass().getDeclaredField(confirmPasswordField);
            confirmPwdFld.setAccessible(true);
            Object confirmPassword = confirmPwdFld.get(value);

            // 如果两个都为空，验证通过（由 @NotBlank 处理）
            if (password == null && confirmPassword == null) {
                return true;
            }

            // 比较两个值
            return password != null && password.equals(confirmPassword);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("Failed to validate password match", e);
            return false;
        }
    }
}
