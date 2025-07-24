package com.example.yozora.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.example.yozora.common.validator.EmailDuplicateValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

//プロフィール編集用
@Documented
@Constraint(validatedBy = EmailDuplicateValidator.class)
@Target(ElementType.TYPE) // user_idも見るためフォーム全体に付ける
@Retention(RetentionPolicy.RUNTIME)
public @interface UserEmailDuplicateCheck {
    String message() default "このメールアドレスは既に使用されています";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
