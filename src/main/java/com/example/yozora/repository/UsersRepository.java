package com.example.yozora.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.yozora.entity.UsersEntity;
import com.example.yozora.mapper.UserRowMapper;

@Repository
public class UsersRepository {

    private final JdbcTemplate jdbcTemplate;

    // コンストラクタ
    public UsersRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // メールアドレスでユーザーレコード取得（ログイン・表示・編集）
    public UsersEntity findUserByEmail(String email) {

        String sql = "SELECT * FROM users WHERE email = ?";

        UsersEntity entity = jdbcTemplate.queryForObject(sql, new UserRowMapper(), email);

        return entity;
    }

    // メールアドレスの重複チェック：新規登録時のバリデーション
    public boolean isUserEmailExists(String email) {

        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);

        boolean result = false;

        if (count != null && count > 0) {
            result = true;
        }

        return result;
    }

    // ユーザー新規登録
    public int insertUser(UsersEntity entity) {
        // プロフィール画像にデフォルトを設定
        entity.setProfileImage("/images/default_profile.png");

        String sql = "INSERT INTO users (user_name, email, password, profile_image) VALUES (?, ?, ?, ?)";

        Object[] parameters = {
                entity.getUserName(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getProfileImage()
        };

        int row = jdbcTemplate.update(sql, parameters);

        return row;
    }

    // ユーザー情報の更新
    public int updateUser(UsersEntity entity) {

        String sql = "UPDATE users SET user_name = ?, email = ?, password = ?, profile_image = ? WHERE id = ?";

        Object[] parameters = {
                entity.getUserName(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getProfileImage(),
                entity.getId()
        };

        int row = jdbcTemplate.update(sql, parameters);

        return row;
    }

    // ユーザー情報の削除
    public int deleteUser(UsersEntity entity) {

        String sql = "DELETE FROM users WHERE id = ?";

        Object[] parameters = {
                entity.getId()
        };

        int row = jdbcTemplate.update(sql, parameters);

        return row;
    }
}
