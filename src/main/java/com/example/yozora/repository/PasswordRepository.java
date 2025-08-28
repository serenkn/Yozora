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
    public int deleteAllByUserId(Integer userId) {

        String sql = "DELETE FROM password_reset_tokens WHERE user_id = ?";

        int row = jdbcTemplate.update(sql, userId);

        return row;
    }

    // 期限付きtoken発行
    public int insertToken(PasswordResetEntity entity) {

        String sql = "INSERT INTO password_reset_tokens (user_id, token, expires_at) VALUES (?, ?, ?)";

        Object[] parameters = new Object[] {

                entity.getUserId(),
                entity.getToken(),
                Timestamp.valueOf(entity.getExpiresAt())
        };

        int row = jdbcTemplate.update(sql, parameters);

        return row;
    }

    // tokenが有効かつ期限切れでないか確認
    public Integer selectToken(String token) {

        String sql = """
                    SELECT COUNT(*)
                    FROM password_reset_tokens
                    WHERE token = ?
                    AND expires_at > CURRENT_TIMESTAMP
                """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, token);

        if (count == null) {
            count = 0;
        }

        return count;
    }

    // tokenに紐づくuser_id を検索
    public Integer findUserIdByToken(String token) {

        String sql = """
                SELECT user_id
                FROM password_reset_tokens
                WHERE token = ?
                  AND expires_at > CURRENT_TIMESTAMP
                LIMIT 1
                """;

        Integer result = jdbcTemplate.queryForObject(sql, Integer.class, token);

        return result;
    }

    // 使い終わったトークンを1件だけ削除（再設定完了時に使用）
    public int deleteByToken(String token) {

        String sql = "DELETE FROM password_reset_tokens WHERE token = ?";

        int row = jdbcTemplate.update(sql, token);

        return row;
    }
}