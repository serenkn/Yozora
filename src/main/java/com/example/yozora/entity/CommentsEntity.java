package com.example.yozora.entity;

import lombok.Data;

import java.time.LocalDateTime;

//commentsテーブル
@Data
public class CommentsEntity {

    private Integer id;
    private Integer postId;
    private Integer userId;
    private String text;
    private LocalDateTime createdAt;

}
