package com.example.yozora.entity;

import lombok.Data;

//followsテーブル
@Data
public class FollowsEntity {

    private Integer id;
    private Integer followerId;
    private Integer followedId;

}
