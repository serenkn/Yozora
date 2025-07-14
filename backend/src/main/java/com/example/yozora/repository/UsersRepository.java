package com.example.yozora.repository;

import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.yozora.entity.UsersEntity;

@Repository
public class UsersRepository {

    private final JdbcTemplate jdbcTemplate;

    // コンストラクタ
    public UsersRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ログイン時、メアド検索
    public Map<String, Object> findUserByEmail(String email) throws Exception {

        String sql = "SELECT * FROM usersTable WHERE mail = ?";

        Map<String, Object> resultMap = jdbcTemplate.queryForMap(sql, email);

        return resultMap;
    }

    // 新規登録時、メアド重複チェック
    public boolean isUserEmailExists(String email) {

        String sql = "SELECT COUNT(*) FROM users WHERE user_email = ?";

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);

        boolean result = false;

        if (count != null && count > 0) {
            result = true;
        }

        return result;
    }

    // 新規登録
    public int userInsert(UsersEntity entity) {

        String sql = "INSERT INTO usersTable (user_name, mail, password) VALUES (?, ?, ?)";

        Object[] parameters = {
                entity.getUserName(),
                entity.getEmail(),
                entity.getPassword()
        };

        int numberOfRow = jdbcTemplate.update(sql, parameters);

        return numberOfRow;
    }
}
