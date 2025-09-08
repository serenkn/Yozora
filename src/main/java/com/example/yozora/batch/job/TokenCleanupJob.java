package com.example.yozora.batch.job;

import com.example.yozora.batch.service.TokenCleanupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TokenCleanupJob {

    private static final Logger log = LoggerFactory.getLogger(TokenCleanupJob.class);
    private final TokenCleanupService tokenCleanupService;

    // コンストラクタ
    public TokenCleanupJob(TokenCleanupService tokenCleanupService) {
        this.tokenCleanupService = tokenCleanupService;
    }

    // 期限切れのパスワードリセットトークンを削除するジョブ
    // 毎時0分に実行（秒 分 時 日 月 曜日）
    @Scheduled(cron = "*/30 * * * * *")
    public void run() {

        log.info("TokenCleanupJob started");

        int deleted = tokenCleanupService.deleteExpiredAll();
        // 件数のログはサービスで出しているため、ここでは未使用
        log.info("TokenCleanupJob finished", deleted);
    }
}