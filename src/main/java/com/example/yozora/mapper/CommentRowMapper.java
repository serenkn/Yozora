package com.example.yozora.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import com.example.yozora.entity.CommentsEntity;

public class CommentRowMapper implements RowMapper<CommentsEntity> {

    @Override
    public CommentsEntity mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {

        CommentsEntity entity = new CommentsEntity();

        entity.setId(rs.getInt("id"));
        entity.setUserId(rs.getInt("user_id"));
        entity.setPostId(rs.getInt("post_id"));
        entity.setText(rs.getString("text"));
        entity.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

        return entity;
    }
}