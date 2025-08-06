package com.example.yozora.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class LikesRepository {

    private final JdbcTemplate jdbcTemplate;

    // コンストラクタ
    public LikesRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // イイネ数検索
    public int LikeCountByPostId(Integer postId) {

        String sql = "SELECT COUNT(*) FROM likes WHERE post_id = ?";

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, postId);

        if (count == null) {
            count = 0;
        }
        return count;
    }

    // 投稿に対し、イイネ済みかチェック
    public boolean likedPost(Integer postId, Integer userId) {

        String sql = "SELECT COUNT(*) FROM likes WHERE post_id = ? AND user_id = ?";

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, postId, userId);

        Boolean result = false;

        if (count != null && count > 0) {

            result = true;// イイネ済み
        }
        return result;
    }

    // いいね追加
    public int insert(Integer postId, Integer userId) {

        String sql = "INSERT INTO likes (post_id, user_id) VALUES (?, ?)";

        int row = jdbcTemplate.update(sql, postId, userId);

        return row;
    }

    // いいね削除
    public int delete(Integer postId, Integer userId) {

        String sql = "DELETE FROM likes WHERE post_id = ? AND user_id = ?";

        int row = jdbcTemplate.update(sql, postId, userId);

        return row;
    }
}
