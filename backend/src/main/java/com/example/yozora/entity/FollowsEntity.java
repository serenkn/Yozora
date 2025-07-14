package com.example.yozora.entity;

import lombok.Data;

//followsテーブル
@Data
public class FollowsEntity {

    private Integer followId;
    private Integer followerUserId;
    private Integer followedUserId;
}
