package com.example.yozora.entity;

import lombok.Data;

//usersテーブル
@Data
public class UsersEntity {

    private Integer userId;
    private String userName;
    private String email;
    private String password;
    private String profileImage;
}
