package com.example.yozora.service;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.yozora.entity.CommentsEntity;
import com.example.yozora.form.CommentForm;
import com.example.yozora.repository.CommentsRepository;

@Service
public class CommentsService {

    private final CommentsRepository commentsRepository;
    private final ModelMapper modelMapper;

    // コンストラクタ
    public CommentsService(CommentsRepository commentsRepository, ModelMapper modelMapper) {
        this.commentsRepository = commentsRepository;
        this.modelMapper = modelMapper;
    }

    // コメント新規追加
    public int insert(CommentForm form) {

        CommentsEntity entity = convertToEntity(form);

        int result = commentsRepository.insert(entity);

        return result;
    }

    // コメント編集
    public int updateComment(CommentForm form) {

        CommentsEntity entity = convertToEntity(form);

        int result = commentsRepository.update(entity);

        return result;
    }

    // コメント削除
    public int deleteComment(Integer commentId) {

        int result = commentsRepository.delete(commentId);

        return result;
    }

    // コメントIDから1件取得（編集用）
    public CommentsEntity findCommentById(Integer commentId) {

        CommentsEntity comment = commentsRepository.findById(commentId);

        return comment;
    }

    // フォーム → エンティティ（登録・編集用）
    private CommentsEntity convertToEntity(CommentForm form) {

        CommentsEntity entity = modelMapper.map(form, CommentsEntity.class);

        return entity;
    }
}