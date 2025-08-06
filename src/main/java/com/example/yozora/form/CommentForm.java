package com.example.yozora.form;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Data
public class CommentForm {

    private Integer Id; // 編集時のみ使用

    @NotNull
    private Integer postId; // 投稿ID

    private Integer userId; // 自動設定（ログイン中ユーザー）

    private String userName; // 自動設定（表示用途）

    @Pattern(regexp = ".*[^\\p{Z}\\s].*", message = "コメントを入力してください。")
    private String text;

}