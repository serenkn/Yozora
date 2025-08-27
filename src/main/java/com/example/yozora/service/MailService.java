package com.example.yozora.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetMail(String to, String link, int expireMinutes) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("【Yozora】パスワード再設定のご案内");
        message.setText("""
                パスワード再設定のご案内です。
                下記のURLより、パスワードの再設定手続きを行ってください。

                %s

                有効期限：%d分
                ※このメールに覚えがない場合は破棄してください。
                """.formatted(link, expireMinutes));
        mailSender.send(message);
    }
}