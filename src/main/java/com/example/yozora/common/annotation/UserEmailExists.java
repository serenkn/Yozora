package com.example.yozora.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.example.yozora.common.validator.EmailExistsValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

//新規登録用
@Documented
@Constraint(validatedBy = EmailExistsValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface UserEmailExists {
    String message() default "このメールアドレスは既に使用されています";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
