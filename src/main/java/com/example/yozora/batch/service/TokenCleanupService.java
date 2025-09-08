package com.example.yozora.batch.service;

import com.example.yozora.repository.PasswordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TokenCleanupService {

    private static final Logger log = LoggerFactory.getLogger(TokenCleanupService.class);
    private final PasswordRepository passwordRepository;

    // コンストラクタ注入
    public TokenCleanupService(PasswordRepository passwordRepository) {

        this.passwordRepository = passwordRepository;

    }

    // 期限切れのパスワードリセットトークンを全件削除して、削除件数を返す
    public int deleteExpiredAll() {
        long started = System.currentTimeMillis();

        int deleted = passwordRepository.deleteExpired();

        long took = System.currentTimeMillis() - started;

        log.info("TokenCleanupService: deleted {} expired tokens ({} ms)", deleted, took);

        return deleted;
    }
}