package com.example.yozora.entity;

import lombok.Data;

import java.time.LocalDateTime;

//commentsテーブル
@Data
public class CommentsEntity {

    private Integer commentId;
    private Integer postId;
    private Integer userId;
    private String commentText;
    private LocalDateTime commentCreatedAt;
    private Boolean isDeleted;
}
