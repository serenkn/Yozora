package com.example.yozora.controller;

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
public class SceneryController {

    private final PostsService postsService;
    private final UsersService usersService;

    // コンストラクタ
    public SceneryController(PostsService postsService, UsersService usersService) {
        this.postsService = postsService;
        this.usersService = usersService;
    }

    // 最新順で一覧表示：初期表示
    @GetMapping(value = "/scenery")
    public String toTop(@AuthenticationPrincipal User loginUser, Model model) {

        // ユーザー情報の取得
        String email = loginUser.getUsername();
        UsersEntity user = usersService.getUserByEmail(email);
        Integer loginUserId = user.getId();
        model.addAttribute("loginUserId", loginUserId);

        List<PostDetailForm> postList = postsService.getAllPosts(loginUserId);

        model.addAttribute("postList", postList);

        return "scenery";
    }

    // 人気順（いいね数）で並び替え
    @GetMapping("/scenery/popular")
    public String toPopular(@AuthenticationPrincipal User loginUser, Model model) {

        // ユーザー情報の取得
        String email = loginUser.getUsername();
        UsersEntity user = usersService.getUserByEmail(email);
        Integer loginUserId = user.getId();

        List<PostDetailForm> postList = postsService.getAllPostsPopular(loginUserId);

        model.addAttribute("postList", postList);

        return "scenery";
    }

    // ランダム順
    @GetMapping("/scenery/random")
    public String toRandom(@AuthenticationPrincipal User loginUser, Model model) {

        // ユーザー情報の取得
        String email = loginUser.getUsername();
        UsersEntity user = usersService.getUserByEmail(email);
        Integer loginUserId = user.getId();

        List<PostDetailForm> postList = postsService.getAllPostsRandom(loginUserId);

        model.addAttribute("postList", postList);

        return "scenery";
    }

    // 過去順
    @GetMapping("/scenery/oldest")
    public String toOldest(@AuthenticationPrincipal User loginUser, Model model) {

        // ユーザー情報の取得
        String email = loginUser.getUsername();
        UsersEntity user = usersService.getUserByEmail(email);
        Integer loginUserId = user.getId();

        List<PostDetailForm> postList = postsService.getAllPostsOldest(loginUserId);

        model.addAttribute("postList", postList);

        return "scenery";
    }

}
