package com.example.yozora.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.yozora.entity.UsersEntity;
import com.example.yozora.service.LikesService;
import com.example.yozora.service.UsersService;

@RestController
public class LikesController {

    private final LikesService likesService;
    private final UsersService usersService;

    public LikesController(LikesService likesService, UsersService usersService) {
        this.likesService = likesService;
        this.usersService = usersService;
    }

    // いいねボタン処理
    @PostMapping(value = "/like/toggle")
    public void toggleLike(@AuthenticationPrincipal User loginUser, @RequestParam("postId") Integer postId) {

        String email = loginUser.getUsername();
        UsersEntity user = usersService.getUserByEmail(email);

        likesService.toggleLike(postId, user.getId());
    }
}