package com.example.yozora.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PasswordResetEntity {

    private Integer id;
    private Integer userId;
    private String token;
    private LocalDateTime expiresAt;// token有効期限
    private LocalDateTime createdAt;

}
