package com.example.yozora.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.yozora.entity.CommentWithUserEntity;
import com.example.yozora.entity.PostAllEntity;
import com.example.yozora.entity.PostImagesEntity;
import com.example.yozora.entity.PostWithImagesEntity;
import com.example.yozora.entity.PostsEntity;
import com.example.yozora.form.PostCreateForm;
import com.example.yozora.form.PostDetailForm;
import com.example.yozora.repository.CommentsRepository;
import com.example.yozora.repository.LikesRepository;
import com.example.yozora.repository.PostsRepository;

@Service
public class PostsService {

    private final PostsRepository postsRepository;
    private final LikesRepository likesRepository;
    private final CommentsRepository commentsRepository;
    private final ModelMapper modelMapper;

    // コンストラクタ
    public PostsService(PostsRepository postsRepository, LikesRepository likesRepository,
            CommentsRepository commentsRepository, ModelMapper modelMapper) {
        this.postsRepository = postsRepository;
        this.likesRepository = likesRepository;
        this.commentsRepository = commentsRepository;
        this.modelMapper = modelMapper;
    }

    // 全ての投稿を取得するメソッド:top画面、scenery画面（最新順）で使用（ログインユーザー用）
    public List<PostDetailForm> getAllPosts(Integer userId) {

        List<PostAllEntity> entityList = postsRepository.findAllPost(userId);

        List<PostDetailForm> entity = convert(entityList);

        return entity;
    }

    // 全ての投稿を取得するメソッド:top画面で使用（ゲストユーザー用）
    public List<PostDetailForm> getAllPostsForGuest() {

        List<PostAllEntity> entityList = postsRepository.findAllPostForGuest();

        List<PostDetailForm> entity = convert(entityList);

        return entity;
    }

    // 投稿IDごとにマッピングしてフォーム変換:top画面で使用
    public List<PostDetailForm> convert(List<PostAllEntity> entityList) {

        Map<Integer, PostDetailForm> postMap = new LinkedHashMap<>();

        for (PostAllEntity entity : entityList) {

            int postId = entity.getId();

            if (!postMap.containsKey(postId)) {
                PostDetailForm form = new PostDetailForm();

                form.setId(postId);
                form.setUserId(entity.getUserId());
                form.setTitle(entity.getTitle());
                form.setAddress(entity.getAddress());
                form.setLatitude(entity.getLatitude());
                form.setLongitude(entity.getLongitude());
                form.setCreatedAt(entity.getCreatedAt());
                form.setUserName(entity.getUserName());
                form.setProfileImage(entity.getProfileImage());
                form.setLikeCount(entity.getLikeCount());
                form.setCommentCount(entity.getCommentCount());
                form.setImageUrls(new ArrayList<>());

                postMap.put(postId, form);
            }

            String imageUrl = entity.getImageUrl();

            if (imageUrl != null && !imageUrl.isEmpty()) {
                postMap.get(postId).getImageUrls().add(imageUrl);
            }
        }

        return new ArrayList<>(postMap.values());
    }

    // 自分の投稿を取得するメソッド:mypageで使用
    public List<PostDetailForm> getMyPosts(Integer userId) {

        List<PostWithImagesEntity> entityList = postsRepository.findMyPosts(userId);

        List<PostDetailForm> postList = convertToForm(entityList);

        // // 各投稿に対して、イイネ、コメント追加
        for (PostDetailForm form : postList) {

            // いいね数を取得
            int likeCount = likesRepository.LikeCountByPostId(form.getId());
            form.setLikeCount(likeCount);

            // // 投稿に対しいいね済みかチェック
            boolean exists = likesRepository.likedPost(form.getId(), userId);
            form.setLiked(exists);

            // コメント数を取得
            int commentCount = commentsRepository.commentCountByPostId(form.getId());
            form.setCommentCount(commentCount);
        }

        return postList;
    }

    // 投稿詳細、コメント取得:投稿は1件
    public PostDetailForm getPostDetail(Integer postId) {

        List<PostWithImagesEntity> entityList = postsRepository.findPost(postId);

        PostDetailForm form = convertToForm(entityList).get(0);

        // いいね数を取得
        int likeCount = likesRepository.LikeCountByPostId(form.getId());
        form.setLikeCount(likeCount);

        // コメント数、一覧を取得
        int commentCount = commentsRepository.commentCountByPostId(postId);
        List<CommentWithUserEntity> commentlist = commentsRepository.findWithUserByPostId(form.getId());
        form.setCommentList(commentlist);
        form.setCommentCount(commentCount);

        return form;
    }

    // 新規投稿
    public int createPost(PostCreateForm form, Integer userId) {
        try {

            PostsEntity postsEntity = convertToEntity(form);

            postsEntity.setUserId(userId);

            // 投稿の保存処理:戻り値は投稿ID
            int postId = postsRepository.insertPost(postsEntity);

            // 画像ファイルの保存処理
            List<String> pathToSave = saveImages(form.getImageFiles());

            List<PostImagesEntity> postImagesEntity = convertToEntity(pathToSave);

            // 画像情報の設定
            int resultRow = 0;
            for (PostImagesEntity entity : postImagesEntity) {

                entity.setPostId(postId);

                resultRow = postsRepository.insertPostImages(entity);
            }
            return resultRow;

        } catch (Exception e) {
            System.err.println("createPostで投稿に失敗しました: " + e.getMessage());
            e.printStackTrace();

            return 0; // 失敗時は0を返す
        }
    }

    // 投稿編集処理（画像以外UPDATE、画像はDELETE→INSERT）
    public int updatePost(PostCreateForm form, Integer userId) {

        try {
            // 投稿情報（画像以外）を更新
            PostsEntity postsEntity = convertToEntity(form);
            postsEntity.setUserId(userId);

            int resultRow = postsRepository.updatePost(postsEntity);

            // 投稿画像を投稿IDで一括削除
            postsRepository.deletePostImages(form.getId());

            // 新たに画像ファイルを保存
            List<String> pathToSave = saveImages(form.getImageFiles());

            // エンティティに変換
            List<PostImagesEntity> postImagesEntity = convertToEntity(pathToSave);

            // 投稿IDをセットし、INSERT
            for (PostImagesEntity entity : postImagesEntity) {
                entity.setPostId(form.getId());
                postsRepository.insertPostImages(entity);
            }

            return resultRow;

        } catch (Exception e) {
            System.err.println("updatePostで失敗: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    // 投稿IDで削除
    public int deletePost(Integer postId) {

        int resultRow = postsRepository.deletePost(postId);

        return resultRow;
    }

    // フォーム → エンティティに変換：新規投稿用(テキストのみ)
    public PostsEntity convertToEntity(PostCreateForm form) {

        PostsEntity entity = modelMapper.map(form, PostsEntity.class);

        return entity;
    }

    // フォーム → エンティティに変換：新規投稿用(画像のみ）
    public List<PostImagesEntity> convertToEntity(List<String> imageUrls) {

        List<PostImagesEntity> entityList = new ArrayList<>();

        int order = 1;

        for (String url : imageUrls) {

            PostImagesEntity entity = new PostImagesEntity();

            entity.setImageUrl(url);
            entity.setImageOrder(order++);
            entityList.add(entity);
        }
        return entityList;
    }

    // 投稿画像付きエンティティリスト → PostDetailFormリストに変換（マイページなどで使用）
    public List<PostDetailForm> convertToForm(List<PostWithImagesEntity> entityList) {

        // 投稿IDごとにフォームをまとめるためのマップ（グルーピング用）
        Map<Integer, PostDetailForm> postMap = new LinkedHashMap<>();

        // 投稿画像付きエンティティを1件ずつ処理
        for (PostWithImagesEntity entity : entityList) {

            int postId = entity.getId(); // 投稿ID取得（1投稿に画像が複数ある前提）

            // 初めてこの投稿IDが登場した場合、フォームを新規作成
            if (!postMap.containsKey(postId)) {

                // entity → PostDetailForm にマッピング
                PostDetailForm form = modelMapper.map(entity, PostDetailForm.class);

                // 投稿IDだけは手動で上書き（念のため）
                form.setId(entity.getId());

                // 最初の画像リストの箱を用意（最初は空）
                form.setImageUrls(new ArrayList<String>());

                // マップに登録（postIdごとにフォームを管理）
                postMap.put(postId, form);
            }

            // 現在の画像URLを取得（nullまたは空文字を除外）
            String imageUrl = entity.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {

                // 念のため null チェックしてから画像リストに追加
                List<String> imageList = postMap.get(postId).getImageUrls();
                if (imageList == null) {
                    imageList = new ArrayList<>();
                    postMap.get(postId).setImageUrls(imageList);
                }

                imageList.add(imageUrl); // 投稿に対応する画像URLを追加
            }
        }

        // 完成した投稿フォームリストを返す（マップ → リストに変換）
        return new ArrayList<PostDetailForm>(postMap.values());
    }

    // 詳細用フォーム → クリエイト用フォームに変換：詳細・編集用
    public PostCreateForm convertToCreateForm(PostDetailForm detailForm) {

        PostCreateForm form = new PostCreateForm();

        form.setId(detailForm.getId());
        form.setUserId(detailForm.getUserId());
        form.setTitle(detailForm.getTitle());
        form.setText(detailForm.getText());
        form.setAddress(detailForm.getAddress());
        form.setLatitude(detailForm.getLatitude());
        form.setLongitude(detailForm.getLongitude());
        form.setImageUrls(detailForm.getImageUrls());

        return form;
    }
    // public PostCreateForm convertToForm(PostsEntity entity) {

    // PostCreateForm form = modelMapper.map(entity, PostCreateForm.class);

    // return form;
    // }

    // 画像ファイルの処理メソッド
    public List<String> saveImages(List<MultipartFile> imageFiles) {
        List<String> imagePaths = new ArrayList<>();

        for (MultipartFile imageFile : imageFiles) {
            if (imageFile != null && !imageFile.isEmpty()) {
                try {
                    // ファイル名生成（UUID + 元ファイル名）
                    String filename = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();

                    // 保存先パス（共通の /images/ ディレクトリ）
                    Path path = Paths.get("images/", filename);
                    Files.createDirectories(path.getParent());

                    // 保存処理
                    Files.copy(imageFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                    // 相対パスでリストに追加
                    imagePaths.add("/images/" + filename);

                } catch (IOException e) {
                    throw new RuntimeException("saveImageで画像の保存に失敗しました: " + imageFile.getOriginalFilename(), e);
                }
            }
        }

        return imagePaths;
    }

}