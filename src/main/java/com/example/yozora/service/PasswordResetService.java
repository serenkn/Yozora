package com.example.yozora.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.yozora.entity.PasswordResetEntity;
import com.example.yozora.entity.UsersEntity;

@Service
public class PasswordResetService {

    private final PasswordResetTokenService passwordResetTokenService;
    private final MailService mailService;

    // コンストラクタ
    public PasswordResetService(PasswordResetTokenService passwordResetTokenService,
            MailService mailService) {
        this.passwordResetTokenService = passwordResetTokenService;
        this.mailService = mailService;
    }

    // メアドを受け取り、トークン発行しメール送信
    public PasswordResetEntity tokenAndSendMail(UsersEntity entity) {

        // UsersEntity → PasswordResetEntity へ変換
        PasswordResetEntity resetEntity = convert(entity);

        // トークン発行
        resetEntity = passwordResetTokenService.issueToken(resetEntity);

        // 有効期限をセット：30分後
        resetEntity.setExpiresAt(LocalDateTime.now().plusMinutes(30));

        // メール送信
        String link = "http://localhost:8080/password/reset/confirm?token=" + resetEntity.getToken();

        mailService.sendPasswordResetMail(entity.getEmail(), link, 30);

        return resetEntity;
    }

    // UsersEntity → PasswordResetEntityに変換
    private PasswordResetEntity convert(UsersEntity entity) {

        PasswordResetEntity entitylist = new PasswordResetEntity();

        entitylist.setUserId(entity.getId());

        return entitylist;
    }

}
