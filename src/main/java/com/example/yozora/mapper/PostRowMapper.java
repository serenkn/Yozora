package com.example.yozora.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import com.example.yozora.entity.PostWithImagesEntity;

//Postsテーブルの中身をPostEntityに変換
public class PostRowMapper implements RowMapper<PostWithImagesEntity> {

    @Override
    public PostWithImagesEntity mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {

        PostWithImagesEntity entity = new PostWithImagesEntity();

        // posts
        entity.setId(rs.getInt("id"));
        entity.setUserId(rs.getInt("user_id"));
        entity.setTitle(rs.getString("title"));
        entity.setText(rs.getString("text"));
        entity.setAddress(rs.getString("address"));
        entity.setLatitude(rs.getDouble("latitude"));
        entity.setLongitude(rs.getDouble("longitude"));
        entity.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

        // users
        entity.setImageUrl(rs.getString("image_url"));
        entity.setImageOrder(rs.getInt("image_order"));

        return entity;
    }
}
