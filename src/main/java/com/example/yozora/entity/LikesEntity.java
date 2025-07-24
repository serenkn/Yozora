package com.example.yozora.entity;

import lombok.Data;

//likesテーブル
@Data
public class LikesEntity {

    private Integer id;
    private Integer userId;
    private Integer postId;

}
