package com.example.yozora.controller;

import com.example.yozora.entity.UsersEntity;
import com.example.yozora.form.CommentForm;
import com.example.yozora.service.CommentsService;
import com.example.yozora.service.UsersService;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
public class CommentsController {

    private final CommentsService commentsService;
    private final UsersService usersService;

    // コンストラクタ
    public CommentsController(CommentsService commentsService, UsersService usersService) {
        this.commentsService = commentsService;
        this.usersService = usersService;
    }

    // コメント新規追加
    @PostMapping(value = "/comment/add")
    public String addCommentByScenerey(
            @Validated @ModelAttribute CommentForm form,
            BindingResult result,
            @AuthenticationPrincipal User loginUser,
            @RequestParam(value = "ret", required = false) String ret,
            Model model) {

        String target = (ret != null && ret.startsWith("/")) ? ret : "/scenery";

        if (result.hasErrors()) {
            return "redirect:" + target;
        }

        String email = loginUser.getUsername();
        UsersEntity user = usersService.getUserByEmail(email);

        form.setUserId(user.getId());
        form.setUserName(user.getUserName());

        int row = commentsService.insert(form);

        if (row == 0) {
            model.addAttribute("error", "コメントに失敗しました。");
        }

        return "redirect:" + target;
    }

    // コメント編集
    @PostMapping("/comment/edit")
    public String editCommentByPost(
            @Validated @ModelAttribute CommentForm form,
            BindingResult result,
            @AuthenticationPrincipal User loginUser,
            @RequestParam(value = "ret", required = false) String ret,
            Model model) {

        String target = (ret != null && ret.startsWith("/")) ? ret : "/scenery";

        if (result.hasErrors()) {

            model.addAttribute("editTargetId", form.getId());

            return "redirect:" + target;
        }

        String email = loginUser.getUsername();
        UsersEntity user = usersService.getUserByEmail(email);

        form.setUserId(user.getId());

        int row = commentsService.updateComment(form);

        if (row == 0) {
            model.addAttribute("error", "コメントの編集できませんでした。");
        }

        return "redirect:" + target;
    }

    // コメント削除処理
    @PostMapping("/comment/delete")
    public String deleteComment(
            @ModelAttribute CommentForm form,
            @AuthenticationPrincipal User loginUser,
            @RequestParam(value = "ret", required = false) String ret,
            Model model) {

        String target = (ret != null && ret.startsWith("/")) ? ret : "/scenery";

        int row = commentsService.deleteComment(form.getId());

        if (row == 0) {
            model.addAttribute("error", "削除に失敗しました。");
            return "redirect:" + target;
        }
        return "redirect:" + target;
    }
}