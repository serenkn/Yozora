package com.example.yozora.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

@Configuration
public class SecurityConfig {

        // 静的リソースをセキュリティ対象外にする設定
        @Bean
        public WebSecurityCustomizer webSecurityCustomizer() {
                return (web) -> web.ignoring().requestMatchers(
                                "/css/**", "/js/**", "/images/**");
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

                http
                                // 認可ルールの設定
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/login", "/userRegist", "/top", "/password/reset",
                                                                "/password/reset/confirm",
                                                                "/css/**", "/js/**")
                                                .permitAll()
                                                .anyRequest().authenticated())

                                // ログイン設定
                                .formLogin(form -> form
                                                .loginPage("/login") // ログイン画面のURL
                                                .loginProcessingUrl("/login") // 認証処理を受けるURL（POST先）
                                                .defaultSuccessUrl("/top", true) // 成功時にTOPへリダイレクト
                                                .failureUrl("/login?error") // 失敗時や未認証アクセス時
                                                .permitAll())

                                // ログアウト設定
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/login?logout")
                                                .permitAll())

                                // 開発中なのでCSRF無効（本番で有効化）
                                .csrf(csrf -> csrf.disable());

                return http.build();
        }

        // パスワードのハッシュ化設定（BCrypt）
        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}
