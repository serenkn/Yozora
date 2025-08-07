package com.example.yozora.controller;

import java.util.ArrayList;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.yozora.entity.UsersEntity;
import com.example.yozora.form.CommentForm;
import com.example.yozora.form.PostCreateForm;
import com.example.yozora.form.PostDetailForm;
import com.example.yozora.service.LikesService;
import com.example.yozora.service.PostsService;
import com.example.yozora.service.UsersService;

import org.springframework.ui.Model;
import jakarta.validation.Valid;

@Controller
public class PostsController {

    private final UsersService usersService;
    private final PostsService postsService;
    private final LikesService likesService;

    // コンストラクタ
    public PostsController(UsersService usersService, PostsService postsService, LikesService likesService) {
        this.usersService = usersService;
        this.postsService = postsService;
        this.likesService = likesService;
    }

    // 新規投稿画面の表示
    @GetMapping(value = "/postCreate")
    public String toPostCreate(@RequestParam("lat") double latitude,
            @RequestParam("lng") double longitude,
            @RequestParam("address") String address,
            Model model) {

        PostCreateForm postForm = new PostCreateForm();

        postForm.setLatitude(latitude); // 緯度をフォームにセット
        postForm.setLongitude(longitude); // 経度をフォームにセット
        postForm.setAddress(address); // 住所をフォームにセット
        postForm.setImageUrls(new ArrayList<>());

        model.addAttribute("postCreateForm", postForm);
        model.addAttribute("pageType", "create");

        return "post_create"; // 投稿作成画面のテンプレート名
    }

    // 投稿編集画面の表示
    @GetMapping(value = "/post/edit/{id}")
    public String toPostEdit(@PathVariable("id") Integer postId,
            @AuthenticationPrincipal User loginUser,
            Model model) {

        // ユーザー情報取得
        String email = loginUser.getUsername();
        UsersEntity user = usersService.getUserByEmail(email);

        // 対象の投稿取得
        PostDetailForm postForm = postsService.getPostDetail(postId);
        PostCreateForm form = postsService.convertToCreateForm(postForm);

        form.setUserId(user.getId());

        model.addAttribute("postCreateForm", form);
        model.addAttribute("pageType", "edit");

        return "post_create";
    }

    // 新規、編集の投稿処理
    @PostMapping(value = "/post/save")
    public String savePost(@AuthenticationPrincipal User loginUser,
            @Valid @ModelAttribute PostCreateForm postForm,
            BindingResult result,
            Model model) {

        // ページタイプ最初に保持
        String pageType = postForm.getPageType();
        model.addAttribute("pageType", pageType);

        if (result.hasErrors()) {
            model.addAttribute("postCreateForm", postForm);
            return "post_create";
        }

        // ユーザー情報の取得
        String email = loginUser.getUsername();
        UsersEntity user = usersService.getUserByEmail(email);

        int rows;

        if (postForm.getId() == null) {
            // 新規投稿
            rows = postsService.createPost(postForm, user.getId());
        } else {
            // 投稿編集
            rows = postsService.updatePost(postForm, user.getId());
        }

        if (rows == 0) {
            model.addAttribute("error", "投稿に失敗しました");
            model.addAttribute("postCreateForm", postForm);

            return "post_create";
        }
        return "redirect:/top";
    }

    // 投稿詳細画面の表示
    @GetMapping(value = "/post")
    public String toPost(@RequestParam("id") Integer postId,
            @AuthenticationPrincipal User loginUser,
            Model model) {

        // ログイン中のユーザー情報の取得
        String email = loginUser.getUsername();
        UsersEntity user = usersService.getUserByEmail(email);

        // 投稿詳細取得
        PostDetailForm form = postsService.getPostDetail(postId);
        PostDetailForm userList = usersService.getUserByPostId(postId);// 投稿者取得
        form.setUserName(userList.getUserName());
        form.setProfileImage(userList.getProfileImage());
        // ログインユーザー取得
        form.setLoginUserId(user.getId());

        // 投稿にいいね済みかチェック
        boolean liked = likesService.likedCheck(postId, user.getId());
        form.setLiked(liked);

        model.addAttribute("post", form);

        // コメント入力用のフォームを渡す
        CommentForm commentForm = new CommentForm();
        commentForm.setPostId(postId);
        model.addAttribute("commentForm", commentForm);

        // 自分の投稿かチェック
        boolean isMyPost = false;
        if (loginUser != null && form != null) {
            Integer loginUserId = user.getId();
            isMyPost = loginUserId.equals(form.getUserId());
        }

        model.addAttribute("isMyPost", isMyPost);

        return "post";
    }
}
