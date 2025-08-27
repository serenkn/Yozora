package com.example.yozora.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

import org.springframework.stereotype.Service;

import com.example.yozora.entity.PasswordResetEntity;
import com.example.yozora.repository.PasswordRepository;

@Service
public class PasswordResetTokenService {

    private static final int TOKEN_BYTES = 32; // 256bit
    private static final long expireMinutes = 30; // 有効期限は固定30分

    private final SecureRandom secureRandom = new SecureRandom();
    private final PasswordRepository passwordRepository;

    public PasswordResetTokenService(PasswordRepository passwordRepository) {
        this.passwordRepository = passwordRepository;
    }

    // トークン発行
    public PasswordResetEntity issueToken(PasswordResetEntity entity) {
        // 既存トークンは全削除（常に最新1枚）
        passwordRepository.deleteAllByUserId(entity.getUserId());

        // 乱数トークン（URLセーフ、paddingなし）
        entity.setToken(generateUrlSafeToken(TOKEN_BYTES));

        // 期限を刻んで保存
        entity.setExpiresAt(LocalDateTime.now().plusMinutes(expireMinutes));

        passwordRepository.insertToken(entity);

        return entity;
    }

    /** URLセーフなランダムトークン生成（Base64URL, no padding） */
    private String generateUrlSafeToken(int byteLength) {

        byte[] buf = new byte[byteLength];

        secureRandom.nextBytes(buf);

        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(buf);

        return token;

    }
}