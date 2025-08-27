package com.example.yozora.controller;

import com.example.yozora.entity.UsersEntity;
import com.example.yozora.form.PasswordEditForm;
import com.example.yozora.form.PasswordResetForm;
import com.example.yozora.service.PasswordResetService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PasswordController {

    private final UsersService usersService;
    private final PasswordResetService passwordResetService;

    // コンストラクタ
    public PasswordController(UsersService usersService, PasswordResetService passwordResetService) {
        this.usersService = usersService;
        this.passwordResetService = passwordResetService;
    }

    // パスワード変更画面の表示
    @GetMapping(value = "/password_edit")
    public String toPasswordEdit(Model model) {

        model.addAttribute("passwordEditForm", new PasswordEditForm());

        return "password_edit";
    }

    // パスワード変更処理
    @PostMapping(value = "/password_edit")
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

    // パスワードを忘れた人はこちらへ画面表示
    @GetMapping(value = "/password/reset")
    public String toPasswordReset(Model model) {

        model.addAttribute("passwordResetForm", new PasswordResetForm());

        return "password_reset";
    }

    // 指定されたメアドにパスワードを再設定画面のリンク送付
    @PostMapping(value = "/password/reset")
    public String passwordReset(@Valid @ModelAttribute PasswordResetForm form,
            BindingResult result,
            RedirectAttributes redirect,
            Model model) {

        if (result.hasErrors()) {
            return "password_reset";
        }

        // 入力されたメールが存在したら
        String email = form.getEmail();
        UsersEntity user = usersService.getUserByEmail(email);

        if (user != null) {

            passwordResetService.tokenAndSendMail(user);
        }

        redirect.addFlashAttribute("message", "パスワード再設定メールをお送りしました。");

        return "redirect:/password/reset?sent=1";
    }
}
