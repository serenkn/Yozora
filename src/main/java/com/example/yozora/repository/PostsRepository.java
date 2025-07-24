package com.example.yozora.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.yozora.entity.PostsEntity;
import com.example.yozora.mapper.PostRowMapper;

@Repository
public class PostsRepository {

    private final JdbcTemplate jdbcTemplate;

    // コンストラクタ
    public PostsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 全ての投稿を取得:top画面で使用 新しい順に
    public List<PostsEntity> findAllPosts() {

        String sql = "SELECT id, user_id, title, text, address, latitude, longitude, created_at FROM posts ORDER BY created_at DESC";

        List<PostsEntity> postsList = jdbcTemplate.query(sql, new PostRowMapper());

        return postsList;
    }

    // 自分の投稿一覧を取得
    public List<PostsEntity> findMyPosts(Integer userId) {

        String sql = "SELECT * FROM posts WHERE user_id = ? ORDER BY created_at DESC";

        List<PostsEntity> postsList = jdbcTemplate.query(sql, new PostRowMapper(), userId);

        return postsList;
    }

    // 投稿IDで投稿1件取得（詳細・編集・削除用）
    public PostsEntity findPost(Integer postId) {

        String sql = "SELECT * FROM posts WHERE id = ?";

        List<PostsEntity> result = jdbcTemplate.query(sql, new PostRowMapper(), postId);

        PostsEntity entity = null;

        if (!result.isEmpty()) {

            entity = result.get(0);
        }
        return entity;
    }

    // 投稿IDで削除実行
    public int deletePost(Integer postId) {

        String sql = "DELETE FROM posts WHERE id = ?";

        int numberOfRow = jdbcTemplate.update(sql, postId);

        return numberOfRow;
    }
}
