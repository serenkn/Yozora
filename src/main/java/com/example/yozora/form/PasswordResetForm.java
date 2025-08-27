package com.example.yozora.form;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordResetForm {

    @NotBlank(message = "メールアドレスを入力してください")
    @Email(message = "メールアドレスの形式ではありません")
    private String email;

    private Integer id;
    private Integer userId;
    private String token;
    private LocalDateTime expiresAt;// token有効期限
    private LocalDateTime createdAt;

}
