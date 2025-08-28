package com.example.yozora.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

import org.springframework.stereotype.Service;

import com.example.yozora.entity.PasswordResetEntity;

//トークン発行用
@Service
public class PasswordResetTokenService {

    private static final int TOKEN_BYTES = 32; // 256bit
    private static final long expireMinutes = 30; // 有効期限は固定30分

    private final SecureRandom secureRandom = new SecureRandom();

    // トークン発行
    public PasswordResetEntity issueToken(PasswordResetEntity entity) {

        // 乱数トークン（URLセーフ、paddingなし）
        entity.setToken(generateUrlSafeToken(TOKEN_BYTES));

        // 期限を刻んで保存
        entity.setExpiresAt(LocalDateTime.now().plusMinutes(expireMinutes));

        return entity;
    }

    // URLセーフなランダムトークン生成（Base64URL, no padding）
    private String generateUrlSafeToken(int byteLength) {

        byte[] buf = new byte[byteLength];// 256bit

        secureRandom.nextBytes(buf);

        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(buf);

        return token;

    }
}