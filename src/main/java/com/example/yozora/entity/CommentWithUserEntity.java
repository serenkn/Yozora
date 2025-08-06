package com.example.yozora.entity;

import lombok.Data;
import java.time.LocalDateTime;

//投稿表示用
@Data
public class CommentWithUserEntity {
    private Integer id;
    private Integer postId;
    private Integer userId;
    private String text;
    private LocalDateTime createdAt;

    private String userName;
    private String profileImage;
}