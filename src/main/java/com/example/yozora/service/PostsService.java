package com.example.yozora.service;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.yozora.entity.PostsEntity;
import com.example.yozora.entity.UsersEntity;
import com.example.yozora.form.PostsForm;
import com.example.yozora.repository.PostsRepository;

@Service
public class PostsService {

    private final PostsRepository postsRepository;
    private final ModelMapper modelMapper;

    // コンストラクタ
    public PostsService(PostsRepository postsRepository, ModelMapper modelMapper) {
        this.postsRepository = postsRepository;
        this.modelMapper = modelMapper;
    }

    // 全ての投稿を取得するメソッド:top画面で使用
    public List<PostsForm> getAllPosts() {

        List<PostsEntity> entityList = postsRepository.findAllPosts();

        List<PostsForm> formList = new ArrayList<>();

        for (PostsEntity entity : entityList) {

            PostsForm form = modelMapper.map(entity, PostsForm.class);

            formList.add(form);
        }

        return formList;
    }

    // 自分の投稿を取得するメソッド:mypageで使用
    public List<PostsForm> getMyPosts(Integer userId) {

        List<PostsEntity> entityList = postsRepository.findMyPosts(userId);

        List<PostsForm> formList = new ArrayList<>();

        for (PostsEntity entity : entityList) {

            PostsForm form = modelMapper.map(entity, PostsForm.class);

            formList.add(form);
        }
        return formList;
    }

    // 投稿IDで投稿1件取得（詳細・編集・削除共通）
    public PostsEntity getPost(Integer postId) {

        PostsEntity entity = postsRepository.findPost(postId);

        return entity;
    }

    // 投稿IDで削除
    public int deletePost(Integer postId) {

        int result = postsRepository.deletePost(postId);

        return result;
    }

    // 投稿をUsersEntityに変換
    public UsersEntity convertToEntity(PostsForm form) {
        return modelMapper.map(form, UsersEntity.class);
    }

    // 投稿をPostsFormに変換
    public PostsForm convertToForm(PostsEntity entity) {
        return modelMapper.map(entity, PostsForm.class);
    }
}