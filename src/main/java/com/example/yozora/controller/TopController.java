package com.example.yozora.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.yozora.form.PostsForm;
import com.example.yozora.service.PostsService;

@Controller
public class TopController {

    private final PostsService postsService;

    // コンストラクタ
    public TopController(PostsService postsService) {
        this.postsService = postsService;
    }

    // トップ画面の表示
    @GetMapping("/top")
    public String toTop(Model model) {

        List<PostsForm> postList = new ArrayList<>();
        // 全ての投稿を取得
        List<PostsForm> fetchedList = postsService.getAllPosts();

        if (fetchedList != null) {

            postList = fetchedList;
        }

        model.addAttribute("postList", postList);

        return "top";

    }
}