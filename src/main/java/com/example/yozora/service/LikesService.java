package com.example.yozora.service;

import org.springframework.stereotype.Service;

import com.example.yozora.repository.LikesRepository;

@Service
public class LikesService {

    private final LikesRepository likesRepository;

    public LikesService(LikesRepository likesRepository) {
        this.likesRepository = likesRepository;
    }

    // いいねの状態取得し、トグルを切り替える
    public int toggleLike(Integer postId, Integer userId) {

        boolean exists = likesRepository.likedPost(postId, userId);

        int row;
        // すでにユーザーがこの投稿に対していいねしているかチェック
        if (exists) {

            row = likesRepository.delete(postId, userId);// いいねしてたら削除

        } else {

            row = likesRepository.insert(postId, userId);// していなかったらtrueを返す：true = イイネ追加
        }
        return row; // いいね追加
    }

    // いいねの状態取得
    public boolean likedCheck(Integer postId, Integer userId) {

        boolean exists = likesRepository.likedPost(postId, userId);

        return exists;
    }

    // 投稿IDからいいね数をを取得
    public int countLikes(int postId) {

        int likeCount = likesRepository.LikeCountByPostId(postId);

        return likeCount;
    }
}
