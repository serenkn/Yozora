package com.example.yozora.form;

import java.sql.Timestamp;

import lombok.Data;

//postsテーブル
@Data
public class PostsForm {

    private Integer id;
    private Integer userId;
    private String title;
    private String text;
    private String address;
    private Double latitude;// 緯度
    private Double longitude;// 経度
    private Timestamp createdAt;

}
