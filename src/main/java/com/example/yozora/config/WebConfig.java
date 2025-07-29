package com.example.yozora.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // 静的リソースのハンドリング設定
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:images/");
    }
}
