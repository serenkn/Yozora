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
    public String toUserRegist(Model model) {

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
            redirectAttributes.addFlashAttribute("message", "登録が完了しました。");

            usersService.authenticateUser(form.getEmail());

            return "redirect:/top";
        }
    }

    // プロフィール変更画面表示
    @GetMapping(value = "/userEdit")
    public String toUserUpdate(@AuthenticationPrincipal User loginUser, Model model) {
        // ユーザー情報の取得
        String email = loginUser.getUsername();
        UsersEntity user = usersService.getUserByEmail(email);
        // ユーザー情報をフォームに変換
        UserEditForm form = usersService.convertToForm(user);
        // プロフィール画像のパスをフォームにセット
        form.setProfileImage(user.getProfileImage());

        model.addAttribute("userEditForm", form);

        return "user_edit";
    }

    // プロフィール変更処理
    @PostMapping(value = "/userEdit")
    public String userUpdate(@AuthenticationPrincipal User loginUser,
            @Validated @ModelAttribute UserEditForm form,
            BindingResult result,
            Model model) {

        // 新しい画像が選択されているか確認
        boolean newImageSelected = form.getProfileImage() != null && !form.getProfileImage().isEmpty();

        if (result.hasErrors()) {

            // 新しい画像が選択されている場合、画像URLをクリア
            if (newImageSelected) {
                model.addAttribute("imageError", "選んだ画像はクリアされました。もう一度選択してください。");
            }

            return "user_edit";
        }

        // // ユーザー情報の取得:id
        String email = loginUser.getUsername();
        UsersEntity user = usersService.getUserByEmail(email);
        form.setId(user.getId());

        // ユーザー情報の更新
        usersService.editUser(form);

        return "redirect:/mypage";
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
