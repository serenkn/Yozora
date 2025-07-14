package com.example.yozora.entity;

import lombok.Data;

import java.time.LocalDateTime;

//postsテーブル
@Data
public class PostsEntity {

    private Integer postId;
    private Integer userId;
    private Integer locationId;
    private String postText;
    private LocalDateTime postCreatedAt;
}
