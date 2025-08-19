package com.example.yozora.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.yozora.entity.CommentsEntity;
import com.example.yozora.entity.CommentWithUserEntity;
import com.example.yozora.mapper.CommentRowMapper;
import com.example.yozora.mapper.CommentWithUserRowMapper;

@Repository
public class CommentsRepository {

    private final JdbcTemplate jdbcTemplate;

    public CommentsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // コメント登録
    public int insert(CommentsEntity entity) {

        String sql = "INSERT INTO comments (post_id, user_id, text) VALUES (?, ?, ?)";

        Object[] params = {
                entity.getPostId(),
                entity.getUserId(),
                entity.getText()
        };

        int result = jdbcTemplate.update(sql, params);

        return result;
    }

    // コメント更新
    public int update(CommentsEntity entity) {

        String sql = "UPDATE comments SET comment_text = ? WHERE id = ? AND users_id = ?";

        Object[] params = {
                entity.getText(),
                entity.getId(),
                entity.getUserId()
        };

        int result = jdbcTemplate.update(sql, params);

        return result;
    }

    // コメント削除
    public int delete(Integer commentId) {

        String sql = "DELETE FROM comments WHERE id = ?";

        int result = jdbcTemplate.update(sql, commentId);

        return result;
    }

    // コメントIDから投稿ID取得
    public Integer getPostIdByCommentId(Integer commentId) {

        String sql = "SELECT posts_id FROM comments WHERE id = ?";

        Integer postId = jdbcTemplate.queryForObject(sql, Integer.class, commentId);

        return postId;
    }

    // コメント数検索
    public int commentCountByPostId(Integer postId) {

        String sql = "SELECT COUNT(*) FROM comments WHERE post_id = ?";

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, postId);

        if (count == null) {
            count = 0;
        }
        return count;
    }

    // コメントIDから1件取得（編集用）
    public CommentsEntity findById(Integer commentId) {

        String sql = "SELECT * FROM comments WHERE id = ?";

        CommentsEntity comment = jdbcTemplate.queryForObject(sql, new CommentRowMapper(), commentId);

        return comment;
    }

    // コメント一覧全取得：コメント + コメント者
    public List<CommentWithUserEntity> findCommentWithUser() {

        String sql = """
                SELECT
                    c.id,
                    c.post_id,
                    c.user_id,
                    c.text,
                    c.created_at,
                    u.user_name,
                    u.profile_image
                FROM comments c
                JOIN users u ON c.user_id = u.id
                ORDER BY c.created_at DESC
                """;

        List<CommentWithUserEntity> commentList = jdbcTemplate.query(sql, new CommentWithUserRowMapper());

        return commentList;
    }

    // 投稿IDからコメント+ユーザー情報取得（出力表示用）
    public List<CommentWithUserEntity> findWithUserByPostId(Integer postId) {

        String sql = """
                SELECT
                    c.id,
                    c.post_id,
                    c.user_id,
                    c.text,
                    c.created_at,
                    u.user_name,
                    u.profile_image
                FROM comments c
                JOIN users u ON c.user_id = u.id
                WHERE c.post_id = ?
                ORDER BY c.created_at DESC
                """;

        List<CommentWithUserEntity> commentList = jdbcTemplate.query(sql, new CommentWithUserRowMapper(), postId);

        return commentList;
    }
}