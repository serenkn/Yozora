package com.example.yozora.controller;

import com.example.yozora.entity.UsersEntity;
import com.example.yozora.entity.PostsEntity;
import com.example.yozora.form.PostsForm;
import com.example.yozora.service.UsersService;
import com.example.yozora.service.PostsService;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class MypageController {

    private final UsersService usersService;
    private final PostsService postsService;

    // コンストラクタ
    public MypageController(UsersService usersService, PostsService postsService) {
        this.usersService = usersService;
        this.postsService = postsService;
    }

    // マイページ画面の表示
    @GetMapping("/mypage")
    public String toMypage(@AuthenticationPrincipal User loginUser, Model model) {

        // ユーザー情報の取得
        String email = loginUser.getUsername();
        UsersEntity user = usersService.getUserByEmail(email);

        model.addAttribute("user", user);

        // 投稿一覧の取得
        List<PostsForm> postList = postsService.getMyPosts(user.getId());

        if (postList != null && !postList.isEmpty()) {
            model.addAttribute("postList", postList);
        } else {
            model.addAttribute("error", "投稿がありません");
        }

        // フォロー・フォロワー数（今は仮）
        model.addAttribute("followCount", 0);
        model.addAttribute("followerCount", 0);

        return "mypage";
    }

    // 自分の投稿削除
    @PostMapping("/post/delete")
    public String deletePost(@ModelAttribute PostsForm form,
            BindingResult result,
            Model model,
            @AuthenticationPrincipal User loginUser) {

        // バリデーションチェック
        if (result.hasErrors()) {
            model.addAttribute("error", "投稿IDの取得に失敗しました");
            return "mypage";
        }

        // ログインユーザー情報
        UsersEntity user = usersService.getUserByEmail(loginUser.getUsername());

        // 投稿取得 & 所有者確認
        PostsEntity post = postsService.getPost(form.getId());
        if (post == null || !post.getUserId().equals(user.getId())) {
            model.addAttribute("error", "削除権限がありません");
            return "mypage";
        }

        // 削除処理
        int rows = postsService.deletePost(form.getId());
        if (rows == 0) {
            model.addAttribute("error", "削除に失敗しました");
            return "mypage";
        }

        return "redirect:/mypage";
    }
}
