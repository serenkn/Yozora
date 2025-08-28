package com.example.yozora.service;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.yozora.entity.PasswordResetEntity;
import com.example.yozora.entity.UsersEntity;
import com.example.yozora.repository.PasswordRepository;
import com.example.yozora.repository.UsersRepository;

@Service
public class PasswordResetService {

    private final PasswordResetTokenService passwordResetTokenService;
    private final MailService mailService;
    private final PasswordRepository passwordRepository;
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    // コンストラクタ
    public PasswordResetService(PasswordResetTokenService passwordResetTokenService,
            MailService mailService,
            PasswordRepository passwordRepository,
            UsersRepository usersRepository,
            PasswordEncoder passwordEncoder) {

        this.passwordResetTokenService = passwordResetTokenService;
        this.mailService = mailService;
        this.passwordRepository = passwordRepository;
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // メアドを受け取り、トークン発行しメール送信
    @Transactional
    public int tokenAndSendMail(UsersEntity entity) {

        // UsersEntity → PasswordResetEntity へ変換
        PasswordResetEntity resetEntity = convert(entity);

        // トークン発行
        resetEntity = passwordResetTokenService.issueToken(resetEntity);

        // 過去のトークン全削除し、追加
        int resultRow = passwordRepository.deleteAllByUserId(resetEntity.getUserId());
        resultRow += passwordRepository.insertToken(resetEntity);

        // 有効期限をセット：30分後
        resetEntity.setExpiresAt(LocalDateTime.now().plusMinutes(30));

        // リンクにトークン付与
        String link = "http://localhost:8080/password/reset/confirm?token=" + resetEntity.getToken();

        // メール送信
        mailService.sendPasswordResetMail(entity.getEmail(), link, 30);

        return resultRow;
    }

    // トークンが有効かつ存在するかチェック
    public int findToken(String token) {

        int resultRow = passwordRepository.selectToken(token);

        return resultRow;
    }

    // tokenに紐づく user_id でパスワードを更新
    @Transactional
    public int updatePasswordByToken(String token, String password) {

        // tokenに紐づくuser_id検索
        Integer userId = passwordRepository.findUserIdByToken(token);

        // 新しいパスワードをハッシュ化してセット
        String hashedNewPassword = passwordEncoder.encode(password);

        // パスワード更新
        int updated = usersRepository.updatePassword(userId, hashedNewPassword);

        // トークン削除（ワンタイムパス）
        int deleted = passwordRepository.deleteByToken(token);

        int row = 0;

        // 更新、削除、失敗チェック
        if (updated != 0 && deleted != 0) {
            row = 1;
        }
        return row;
    }

    // UsersEntity → PasswordResetEntityに変換
    private PasswordResetEntity convert(UsersEntity entity) {

        PasswordResetEntity entitylist = new PasswordResetEntity();

        entitylist.setUserId(entity.getId());

        return entitylist;
    }

}
