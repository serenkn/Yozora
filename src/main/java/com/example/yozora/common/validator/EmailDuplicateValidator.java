package com.example.yozora.common.validator;

import com.example.yozora.common.annotation.UserEmailDuplicateCheck;
import com.example.yozora.form.UserEditForm;
import com.example.yozora.repository.UsersRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// プロフィール変更用：メールアドレス重複チェックバリデータ
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
        // Tableに登録済みのemailがあればfalse（ログイン中ユーザーは対象外）
        boolean isAvailable = usersRepository.isDuplicateEmail(email, userId);

        return isAvailable;
    }
}
