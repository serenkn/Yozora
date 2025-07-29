package com.example.yozora.controller;

import com.example.yozora.form.PasswordEditForm;
import com.example.yozora.service.UsersService;

import jakarta.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PasswordController {

    private final UsersService usersService;

    // コンストラクタ
    public PasswordController(UsersService usersService) {
        this.usersService = usersService;
    }

    // パスワード変更画面の表示
    @GetMapping("/password_edit")
    public String toPasswordEdit(Model model) {

        model.addAttribute("passwordEditForm", new PasswordEditForm());

        return "password_edit";
    }

    // パスワード変更処理
    @PostMapping("/password_edit")
    public String updatePassword(
            @AuthenticationPrincipal User loginUser,
            @Valid @ModelAttribute PasswordEditForm form,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            return "password_edit";
        }

        String email = loginUser.getUsername();

        boolean success = usersService.updatePassword(email, form);

        if (!success) {
            model.addAttribute("error", "現在のパスワードが一致しません");
            return "password_edit";
        }

        model.addAttribute("message", "パスワードを変更しました");
        return "redirect:/mypage";
    }
}
