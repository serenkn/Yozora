package com.example.yozora.common.validator;

import com.example.yozora.common.annotation.UserEmailDuplicateCheck;
import com.example.yozora.entity.UsersEntity;
import com.example.yozora.form.UserEditForm;
import com.example.yozora.repository.UsersRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// ユーザー編集用のメールアドレス重複チェックバリデータ
@Component
public class EmailDuplicateValidator implements ConstraintValidator<UserEmailDuplicateCheck, UserEditForm> {

    @Autowired
    private UsersRepository usersRepository;

    @Override
    public boolean isValid(UserEditForm form, ConstraintValidatorContext context) {
        String email = form.getEmail();
        Integer userId = form.getId();

        if (email == null) {
            return true; // メール空は別バリデーションでチェック
        }

        UsersEntity existingUser = usersRepository.findUserByEmail(email);

        // 他のユーザーが使っている場合はNG（自分自身はOK）
        if (existingUser != null && !existingUser.getId().equals(userId)) {
            return false;
        }

        return true;
    }
}
