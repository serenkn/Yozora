package com.example.yozora.form;

import org.springframework.web.multipart.MultipartFile;

import com.example.yozora.common.annotation.UserEmailDuplicateCheck;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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

    private MultipartFile image;// 新規の画像ファイルが選択された時用

    private String profileImage;// プロフィール画像のパス

}
