package com.example.yozora.entity;

import lombok.Data;

//postImageテーブル
@Data
public class PostImagesEntity {

    private Integer id;
    private Integer postId;
    private String imageUrl;
    private Integer imageOrder;

}
