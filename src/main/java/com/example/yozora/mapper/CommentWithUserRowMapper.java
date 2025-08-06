package com.example.yozora.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import com.example.yozora.entity.CommentWithUserEntity;

import org.springframework.lang.NonNull;

public class CommentWithUserRowMapper implements RowMapper<CommentWithUserEntity> {
    @Override
    public CommentWithUserEntity mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {

        CommentWithUserEntity entity = new CommentWithUserEntity();

        entity.setId(rs.getInt("id"));
        entity.setPostId(rs.getInt("post_id"));
        entity.setUserId(rs.getInt("user_id"));
        entity.setText(rs.getString("text"));
        entity.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

        // JOINで取得したユーザー情報
        entity.setUserName(rs.getString("user_name"));
        entity.setProfileImage(rs.getString("profile_image"));

        return entity;
    }
}