package com.example.yozora.form;

import lombok.Data;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import com.example.yozora.common.annotation.UserEmailExists;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
public class UserRegistForm {

    @NotEmpty(message = "未入力項目があります")
    private String userName;

    @NotBlank(message = "未入力項目があります")
    @Email(message = "メールアドレスの形式ではありません")
    @UserEmailExists(message = "メールアドレスが重複しています")
    private String email;

    @NotBlank(message = "未入力項目があります")
    @Pattern(regexp = "^[!-~]{8,}$", message = "パスワードは半角英数字記号のみの8文字以上で入力してください")
    private String password;

}
