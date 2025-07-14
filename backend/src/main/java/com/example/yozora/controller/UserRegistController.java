package com.example.yozora.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.yozora.form.RegistUserForm;
import com.example.yozora.service.UsersService;

@Controller
public class UserRegistController {

    private final UsersService usersService;

    // コンストラクタ
    public UserRegistController(UsersService usersService) {
        this.usersService = usersService;
    }

    // 新規登録画面表示
    @GetMapping("/userRegister")
    public String toRegist(Model model) {

        RegistUserForm registUserForm = new RegistUserForm();

        model.addAttribute("registUserForm", registUserForm);

        return "/userRegister";
    }

    // 入力データをDBに保存
    @PostMapping("/userRegister")
    public String userRegist(@Validated @ModelAttribute RegistUserForm form,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) throws Exception {

        if (result.hasErrors()) {
            return "userRegister";
        }

        int numberOfRow = usersService.registUser(form);

        if (numberOfRow == 0) {

            model.addAttribute("error", "登録に失敗しました");

            return "userRegister";
        } else {

            redirectAttributes.addFlashAttribute("success", "登録が完了しました");

            usersService.authenticateUser(form.getEmail());

            return "redirect:/top";
        }
    }

}
