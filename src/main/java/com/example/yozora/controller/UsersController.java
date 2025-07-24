package com.example.yozora.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.yozora.entity.UsersEntity;
import com.example.yozora.form.UserEditForm;
import com.example.yozora.form.UserRegistForm;
import com.example.yozora.service.UsersService;

@Controller
public class UsersController {

    private final UsersService usersService;

    // コンストラクタ
    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    // 新規登録画面表示
    @GetMapping(value = "/userRegist")
    public String toRegist(Model model) {

        model.addAttribute("userRegistForm", new UserRegistForm());

        return "user_regist";
    }

    // 新規登録処理
    @PostMapping(value = "/userRegist")
    public String userRegist(@Validated @ModelAttribute UserRegistForm form,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "user_regist";
        }

        int numberOfRow = usersService.registUser(form);

        if (numberOfRow == 0) {

            model.addAttribute("error", "登録に失敗しました");

            return "user_regist";

        } else {
            redirectAttributes.addFlashAttribute("message", "登録が完了しました。ログインしてください。");

            usersService.authenticateUser(form.getEmail());

            return "redirect:/top";
        }
    }

    // プロフィール変更画面表示
    @GetMapping("/userEdit")
    public String toUserUpdate(@AuthenticationPrincipal User loginUser, Model model) {

        // ユーザー情報の取得
        String email = loginUser.getUsername();

        UsersEntity user = usersService.getUserByEmail(email);

        model.addAttribute("userEditForm", user);

        return "user_edit";
    }

    // プロフィール変更処理
    @PostMapping("/userEdit")
    public String userUpdate(@Validated @ModelAttribute UserEditForm form,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            return "user_edit";
        }

        // ユーザー情報の更新
        int numberOfRow = usersService.editUser(form);

        if (numberOfRow == 0) {

            model.addAttribute("error", "変更に失敗しました");

            return "user_edit";

        } else {

            model.addAttribute("message", "プロフィールを変更しました");

            return "redirect:/mypage";
        }
    }

    // ユーザ退会処理
    @PostMapping(value = "/userDelete")
    public String toDeleteUser(@AuthenticationPrincipal User loginUser, Model model,
            RedirectAttributes redirectAttributes) {

        // ユーザー情報の取得
        String email = loginUser.getUsername();

        UsersEntity user = usersService.getUserByEmail(email);

        // ユーザ情報の削除
        int numberOfRow = usersService.userDelete(user);

        if (numberOfRow == 0) {

            model.addAttribute("error", "退会に失敗しました");
            return "user_edit";
        } else {

            SecurityContextHolder.clearContext();// 認証削除

            redirectAttributes.addFlashAttribute("message", "退会しました。ご利用ありがとうございました。");

            return "redirect:/login";
        }

    }
}
