package com.example.yozora.form;

import com.example.yozora.common.annotation.UserEmailDuplicateCheck;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@UserEmailDuplicateCheck(message = "メールアドレスが重複しています")
@Data
public class UserEditForm {

    private Integer id;

    @NotEmpty(message = "未入力項目があります")
    private String userName;

    @NotBlank(message = "未入力項目があります")
    @Email(message = "メールアドレスの形式ではありません")
    private String email;

    @NotBlank(message = "未入力項目があります")
    @Pattern(regexp = "^[!-~]{8,}$", message = "パスワードは半角英数字記号のみの8文字以上で入力してください")
    private String password;

    private String profileImage;

}
