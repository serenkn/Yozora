package com.example.yozora.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PasswordResetForm {

    @NotBlank(message = "未入力項目があります")
    @Pattern(regexp = "^[!-~]{8,}$", message = "パスワードは半角英数字記号のみの8文字以上で入力してください")
    private String password;

    @NotBlank(message = "未入力項目があります")
    @Pattern(regexp = "^[!-~]{8,}$", message = "パスワードは半角英数字記号のみの8文字以上で入力してください")
    private String confirmPassword;

    private String token;
}
