package com.example.yozora.common.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.yozora.common.annotation.UserEmailExists;
import com.example.yozora.repository.UsersRepository;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

// 新規登録用：メールアドレス重複チェックバリデータ
@Component
public class EmailExistsValidator implements ConstraintValidator<UserEmailExists, String> {

    @Autowired
    private UsersRepository usersRepository;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {

        if (email == null || email.isEmpty()) {
            return true; // 空
        }
        // Tableに登録済みのemailがあればfalse
        Boolean isValidEmail = usersRepository.isUserEmailExists(email);

        return isValidEmail;
    }
}