package com.example.yozora.repository;

import org.springframework.dao.EmptyResultDataAccessException;
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

    // 新規登録用：メールアドレスの重複チェック
    public boolean isUserEmailExists(String email) {

        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);

        boolean result = true; // 重複していないと仮定

        if (count != null && count > 0) {
            result = false; // 重複している
        }

        return result;
    }

    // 新規登録用：ユーザー情報の登録
    public int insertUser(UsersEntity entity) {
        // プロフィール画像にデフォルトを設定
        entity.setProfileImage("/images/default_icon.png");

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

    // プロフィール変更用：ユーザー情報の更新
    public int updateUser(UsersEntity entity) {

        String sql = "UPDATE users SET user_name = ?, email = ?, profile_image = ? WHERE id = ?";

        Object[] parameters = {
                entity.getUserName(),
                entity.getEmail(),
                entity.getProfileImage(),
                entity.getId()
        };

        int row = jdbcTemplate.update(sql, parameters);

        return row;
    }

    // プロフィール変更用：メールアドレスの重複チェック（本人ならOK）
    public boolean isDuplicateEmail(String email, Integer selfId) {
        // 入力されたメアドが使われてるか確認
        String sql = "SELECT id FROM users WHERE email = ?";

        try {
            Integer existingUserId = jdbcTemplate.queryForObject(sql, Integer.class, email);
            // メールアドレスが存在しない場合は重複していない
            if (existingUserId == null) {
                return true;
            }
            // 自分自身のIDなら重複していない
            if (existingUserId.equals(selfId)) {
                return true;
            }

            // 他人のIDなら重複
            return false;

        } catch (EmptyResultDataAccessException e) {
            // メールアドレスが見つからない → 重複していない
            return true;
        }
    }

    // アカウント退会用：ユーザー情報の削除
    public int deleteUser(UsersEntity entity) {

        String sql = "DELETE FROM users WHERE id = ?";

        Object[] parameters = {
                entity.getId()
        };

        int row = jdbcTemplate.update(sql, parameters);

        return row;
    }

    // パスワードの更新
    public int updatePassword(Integer userId, String hashedPassword) {

        String sql = "UPDATE users SET password = ? WHERE id = ?";

        int row = jdbcTemplate.update(sql, hashedPassword, userId);

        return row;
    }
}
