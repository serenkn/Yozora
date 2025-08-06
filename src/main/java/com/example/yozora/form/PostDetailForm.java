package com.example.yozora.form;

import java.time.LocalDateTime;
import java.util.List;

import com.example.yozora.entity.CommentWithUserEntity;

import lombok.Data;

// 投稿フォーム:出力用
@Data
public class PostDetailForm {

    private Integer id; // 投稿ID

    private Integer userId;// 投稿者ID
    private Integer loginUserId; // ログインユーザーID

    private String title;// 投稿タイトル

    private String text;// 投稿詳細

    private String address;// 住所

    private Double latitude;// 緯度

    private Double longitude;// 経度

    private List<String> imageUrls;// 投稿画像表示用

    private int likeCount;// いいね数
    private boolean liked; // イイネ済みか確認用

    private int commentCount;// コメント数

    private List<CommentWithUserEntity> commentList;// コメント一覧

    private LocalDateTime createdAt;// 投稿日

    private String userName;// ユーザーネーム

    private String profileImage;// アイコン

}