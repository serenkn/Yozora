package com.example.yozora.entity;

import lombok.Data;

import java.time.LocalDateTime;

//postsテーブル
@Data
public class PostsEntity {

    private Integer id;
    private Integer userId;
    private String title;
    private String text;
    private String address;
    private Double latitude;// 緯度
    private Double longitude;// 経度
    private LocalDateTime createdAt;

}
