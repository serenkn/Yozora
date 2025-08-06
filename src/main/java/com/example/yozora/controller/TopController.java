package com.example.yozora.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.yozora.entity.UsersEntity;
import com.example.yozora.form.PostDetailForm;
import com.example.yozora.service.PostsService;
import com.example.yozora.service.UsersService;

@Controller
public class TopController {

    private final PostsService postsService;
    private final UsersService usersService;

    // コンストラクタ
    public TopController(PostsService postsService, UsersService usersService) {
        this.postsService = postsService;
        this.usersService = usersService;
    }

    // トップ画面の表示
    @GetMapping(value = "/top")
    public String toTop(@AuthenticationPrincipal User loginUser, Model model) {

        List<PostDetailForm> postList = new ArrayList<>();

        if (loginUser != null) {

            // ユーザー情報の取得
            String email = loginUser.getUsername();
            UsersEntity user = usersService.getUserByEmail(email);
            Integer loginUserId = user.getId();

            // 全ての投稿を取得：ログインユーザー用
            postList = postsService.getAllPosts(loginUserId);
        } else {
            // ゲストログイン用
            postList = postsService.getAllPostsForGuest();
        }

        model.addAttribute("postList", postList);

        return "top";

    }
}