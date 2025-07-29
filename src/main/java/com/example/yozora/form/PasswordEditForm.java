package com.example.yozora.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PasswordEditForm {

    @NotBlank(message = "現在のパスワードを入力してください")
    private String currentPassword;

    @NotBlank(message = "新しいパスワードを入力してください")
    @Pattern(regexp = "^[!-~]{8,}$", message = "パスワードは半角英数字記号のみの8文字以上で入力してください")
    private String newPassword;
}
