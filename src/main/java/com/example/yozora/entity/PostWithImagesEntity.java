package com.example.yozora.entity;

import java.time.LocalDateTime;

import lombok.Data;

//postsテーブルとpost_imagesテーブルの紐づけ用:投稿全て
@Data
public class PostWithImagesEntity {

    private Integer Id;
    private Integer userId;
    private String title;
    private String text;
    private String address;
    private Double latitude;
    private Double longitude;
    private LocalDateTime createdAt;

    private String imageUrl;
    private Integer imageOrder;

}
