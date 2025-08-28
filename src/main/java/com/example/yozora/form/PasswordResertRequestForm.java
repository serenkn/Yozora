package com.example.yozora.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordResertRequestForm {

    @NotBlank(message = "メールアドレスを入力してください")
    @Email(message = "メールアドレスの形式ではありません")
    private String email;

}
