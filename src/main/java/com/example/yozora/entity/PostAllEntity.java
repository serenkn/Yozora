package com.example.yozora.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PostAllEntity {

    // 投稿
    private Integer id;
    private Integer userId;
    private String title;
    private String address;
    private Double latitude;
    private Double longitude;
    private LocalDateTime createdAt;

    // 画像
    private String imageUrl;
    private Integer imageOrder;

    // ユーザー
    private String userName;
    private String profileImage;

    // いいね数
    private Integer likeCount;
    private Boolean liked;

    // コメント数
    private Integer commentCount;

}