package com.example.yozora.repository;

import java.sql.Timestamp;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.yozora.entity.PasswordResetEntity;

@Repository
public class PasswordRepository {

    private final JdbcTemplate jdbcTemplate;

    // コンストラクタ
    public PasswordRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // user_idでtokenを一括削除
    public void deleteAllByUserId(Integer userId) {

        String sql = "DELETE FROM password_reset_tokens WHERE user_id = ?";

        jdbcTemplate.update(sql, userId);
    }

    // 期限付きtoken発行
    public void insertToken(PasswordResetEntity entity) {

        String sql = "INSERT INTO password_reset_tokens (user_id, token, expires_at) VALUES (?, ?, ?)";

        Object[] parameters = new Object[] {

                entity.getUserId(),
                entity.getToken(),
                Timestamp.valueOf(entity.getExpiresAt())
        };

        jdbcTemplate.update(sql, parameters);
    }
}