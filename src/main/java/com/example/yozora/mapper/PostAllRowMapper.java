package com.example.yozora.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
// import java.sql.Timestamp;

import org.springframework.jdbc.core.RowMapper;

import com.example.yozora.entity.PostAllEntity;

import org.springframework.lang.NonNull;

public class PostAllRowMapper implements RowMapper<PostAllEntity> {

    @Override
    public PostAllEntity mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {

        PostAllEntity entity = new PostAllEntity();

        // posts
        entity.setId(rs.getInt("id"));
        entity.setUserId(rs.getInt("user_id"));
        entity.setTitle(rs.getString("title"));
        entity.setAddress(rs.getString("address"));
        entity.setLatitude(rs.getDouble("latitude"));
        entity.setLongitude(rs.getDouble("longitude"));
        entity.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

        // post_images
        entity.setImageUrl(rs.getString("image_url"));
        entity.setImageOrder(rs.getInt("image_order"));

        // users
        entity.setUserName(rs.getString("user_name"));
        entity.setProfileImage(rs.getString("profile_image"));

        // likes
        entity.setLikeCount(rs.getInt("like_count"));
        entity.setLiked(rs.getBoolean("liked"));

        // comments
        entity.setCommentCount(rs.getInt("comment_count"));

        return entity;
    }
}