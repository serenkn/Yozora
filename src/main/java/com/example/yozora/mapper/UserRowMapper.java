package com.example.yozora.mapper;

import com.example.yozora.entity.UsersEntity;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;

// Usersテーブルの中身をUsersEntitに変換
public class UserRowMapper implements RowMapper<UsersEntity> {

    @Override
    public UsersEntity mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {

        UsersEntity entity = new UsersEntity();

        entity.setId(rs.getInt("id"));
        entity.setUserName(rs.getString("user_name"));
        entity.setEmail(rs.getString("email"));
        entity.setPassword(rs.getString("password"));
        entity.setProfileImage(rs.getString("profile_image"));

        return entity;
    }
}
