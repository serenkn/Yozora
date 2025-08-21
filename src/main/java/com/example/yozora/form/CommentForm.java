package com.example.yozora.form;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class CommentForm {

    private Integer id; // 編集時のみ使用

    @NotNull
    private Integer postId; // 投稿ID

    private Integer userId; // 自動設定（ログイン中ユーザー）

    private String userName; // 自動設定（表示用途）

    private String text;

}