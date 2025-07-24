package com.example.yozora.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    // ログイン画面を表示するメソッド
    @GetMapping(value = "/login")
    public String toLogin() {
        return "login";
    }
}
