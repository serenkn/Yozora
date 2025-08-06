package com.example.yozora.repository;

import java.sql.PreparedStatement;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.example.yozora.entity.PostAllEntity;
import com.example.yozora.entity.PostImagesEntity;
import com.example.yozora.entity.PostWithImagesEntity;
import com.example.yozora.entity.PostsEntity;
import com.example.yozora.mapper.PostAllRowMapper;
import com.example.yozora.mapper.PostRowMapper;

@Repository
public class PostsRepository {

    private final JdbcTemplate jdbcTemplate;

    // コンストラクタ
    public PostsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ログインユーザー用 投稿、画像、ユーザー名、アイコン、いいね数、コメント数、イイネ済みかチェック 全取得し投稿に結合：投稿最新順
    public List<PostAllEntity> findAllPost(Integer userId) {

        String sql = """
                    SELECT
                        p.id,
                        p.user_id,
                        p.title,
                        p.address,
                        p.latitude,
                        p.longitude,
                        p.created_at,
                        pi.image_url,
                        pi.image_order,
                        u.user_name,
                        u.profile_image,
                        COALESCE(l.like_count, 0) AS like_count,
                        COALESCE(c.comment_count, 0) AS comment_count,
                        CASE
                            WHEN ul.user_id IS NOT NULL THEN TRUE
                            ELSE FALSE
                        END AS liked
                    FROM posts p
                    LEFT JOIN post_images pi ON p.id = pi.post_id
                    LEFT JOIN users u ON p.user_id = u.id
                    LEFT JOIN (
                        SELECT post_id, COUNT(*) AS like_count
                        FROM likes
                        GROUP BY post_id
                    ) l ON p.id = l.post_id
                    LEFT JOIN (
                        SELECT post_id, COUNT(*) AS comment_count
                        FROM comments
                        GROUP BY post_id
                    ) c ON p.id = c.post_id
                    LEFT JOIN (
                        SELECT post_id, user_id
                        FROM likes
                        WHERE user_id = ?
                    ) ul ON p.id = ul.post_id
                    ORDER BY p.created_at DESC
                """;

        List<PostAllEntity> entity = jdbcTemplate.query(sql, new PostAllRowMapper(), userId);

        return entity;
    }

    // ログインユーザー用 投稿、画像、ユーザー名、アイコン、いいね数、コメント数、イイネ済みかチェック 全取得し投稿に結合：投稿最新順
    public List<PostAllEntity> findAllPostForGuest() {

        String sql = """
                    SELECT
                        p.id,
                        p.user_id,
                        p.title,
                        p.address,
                        p.latitude,
                        p.longitude,
                        p.created_at,
                        pi.image_url,
                        pi.image_order,
                        u.user_name,
                        u.profile_image,
                        COALESCE(l.like_count, 0) AS like_count,
                        COALESCE(c.comment_count, 0) AS comment_count,
                        FALSE AS liked
                    FROM posts p
                    LEFT JOIN post_images pi ON p.id = pi.post_id
                    LEFT JOIN users u ON p.user_id = u.id
                    LEFT JOIN (
                        SELECT post_id, COUNT(*) AS like_count
                        FROM likes
                        GROUP BY post_id
                    ) l ON p.id = l.post_id
                    LEFT JOIN (
                        SELECT post_id, COUNT(*) AS comment_count
                        FROM comments
                        GROUP BY post_id
                    ) c ON p.id = c.post_id
                    ORDER BY p.created_at DESC
                """;

        List<PostAllEntity> entity = jdbcTemplate.query(sql, new PostAllRowMapper());

        return entity;
    }

    // 自分の投稿一覧を取得：マイページ画面で使用
    public List<PostWithImagesEntity> findMyPosts(Integer userId) {
        String sql = """
                SELECT
                    p.id,
                    p.user_id,
                    p.title,
                    p.text,
                    p.address,
                    p.latitude,
                    p.longitude,
                    p.created_at,
                    i.image_url,
                    i.image_order
                FROM posts p
                LEFT JOIN post_images i ON p.id = i.post_id
                WHERE p.user_id = ?
                ORDER BY p.created_at DESC, i.image_order ASC
                """;

        List<PostWithImagesEntity> result = jdbcTemplate.query(sql, new PostRowMapper(), userId);

        return result;
    }

    // 投稿IDで投稿1件取得（詳細・編集・削除用）
    public List<PostWithImagesEntity> findPost(Integer postId) {

        String sql = """
                SELECT
                    p.id,
                    p.user_id,
                    p.title,
                    p.text,
                    p.address,
                    p.latitude,
                    p.longitude,
                    p.created_at,
                    i.image_url,
                    i.image_order
                FROM posts p
                LEFT JOIN post_images i ON p.id = i.post_id
                WHERE p.id = ?
                ORDER BY p.created_at DESC, i.image_order ASC
                """;

        List<PostWithImagesEntity> result = jdbcTemplate.query(sql, new PostRowMapper(), postId);

        return result;
    }

    // 新規投稿の作成
    public int insertPost(PostsEntity entity) {

        String sql = "INSERT INTO posts (user_id, title, text, address, latitude, longitude) VALUES (?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        // 自動採番されたIDを取得するためのKeyHolderを使用
        jdbcTemplate.update(connection -> {
            // PreparedStatementを使用してSQLを実行
            PreparedStatement ps = connection.prepareStatement(sql, new String[] { "id" });
            // パラメータを設定:1から順に
            ps.setInt(1, entity.getUserId());
            ps.setString(2, entity.getTitle());
            ps.setString(3, entity.getText());
            ps.setString(4, entity.getAddress());
            ps.setDouble(5, entity.getLatitude());
            ps.setDouble(6, entity.getLongitude());

            return ps;// sqlとパラメータをJDBCに返す

        }, keyHolder);// ㎰と、空箱のkeyHolderをJDBCに渡し、自動採番されたIDが格納される

        Number key = keyHolder.getKey();

        if (key == null) {

            throw new IllegalArgumentException("自動採番失敗しました");
        }
        int postId = key.intValue(); // 自動採番されたIDを取得

        return postId; // 自動採番されたIDを返す
    }

    // 投稿情報（画像以外）を更新
    public int updatePost(PostsEntity entity) {
        String sql = "UPDATE posts SET title = ?, texts = ?, address = ?, latitude = ?, longitude = ? WHERE id = ?";

        Object[] parameters = {
                entity.getTitle(),
                entity.getText(),
                entity.getAddress(),
                entity.getLatitude(),
                entity.getLongitude(),
                entity.getId()
        };

        int row = jdbcTemplate.update(sql, parameters);
        return row;
    }

    // 投稿画像の保存
    public int insertPostImages(PostImagesEntity entity) {

        String sql = "INSERT INTO post_images (post_id, image_url, image_order) VALUES (?, ?, ?)";

        Object[] parameters = {
                entity.getPostId(),
                entity.getImageUrl(),
                entity.getImageOrder(),
        };

        int row = jdbcTemplate.update(sql, parameters);

        return row;
    }

    // 投稿IDで画像を一括削除
    public int deletePostImages(Integer postId) {

        String sql = "DELETE FROM post_images WHERE post_id = ?";

        int numberOfRow = jdbcTemplate.update(sql, postId);

        return numberOfRow;
    }

    // 投稿IDでコメントを一括削除
    public int deletePostComments(Integer postId) {

        String sql = "DELETE FROM comments WHERE post_id = ?";

        int numberOfRow = jdbcTemplate.update(sql, postId);

        return numberOfRow;
    }

    // 投稿IDで投稿削除：画像、コメント以外
    public int deletePost(Integer postId) {

        String sql = "DELETE FROM posts WHERE id = ?";

        int numberOfRow = jdbcTemplate.update(sql, postId);

        return numberOfRow;
    }
}
