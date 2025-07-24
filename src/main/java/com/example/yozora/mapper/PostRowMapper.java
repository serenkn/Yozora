package com.example.yozora.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import com.example.yozora.entity.PostsEntity;

//Postsテーブルの中身をPostEntityに変換
public class PostRowMapper implements RowMapper<PostsEntity> {

    @Override
    public PostsEntity mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {

        PostsEntity entity = new PostsEntity();

        entity.setId(rs.getInt("id"));
        entity.setUserId(rs.getInt("user_id"));
        entity.setTitle(rs.getString("title"));
        entity.setText(rs.getString("text"));
        entity.setAddress(rs.getString("address"));
        entity.setLatitude(rs.getDouble("latitude"));
        entity.setLongitude(rs.getDouble("longitude"));
        entity.setCreatedAt(rs.getTimestamp("created_at"));

        return entity;
    }
}
