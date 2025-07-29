package com.example.yozora.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.yozora.form.PostsForm;

import org.springframework.ui.Model;
import jakarta.validation.Valid;

@Controller
public class PostsController {

    // コンストラクタ
    public PostsController() {
        // 初期化処理が必要な場合はここに記述
    }

    // 新規投稿画面の表示
    @GetMapping("/postCreate")
    public String toPostCreate(@RequestParam("lat") double lat,
            @RequestParam("lng") double lng, Model model) {

        return "post_create"; // 投稿作成画面のテンプレート名
    }

    @PostMapping("/postCreate")
    public String postCreate(@AuthenticationPrincipal User loginUser,
            @Valid @ModelAttribute PostsForm postsForm,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("error", "投稿内容に誤りがあります");
            return "post_create"; // エラーがある場合は再度投稿画面を表示
        }

        // 投稿処理のロジックをここに追加
        // 例: postsService.createPost(postForm, loginUser.getUsername());

        model.addAttribute("message", "投稿が完了しました");
        return "redirect:/top"; // 投稿完了後はトップページへリダイレクト
    }

}
