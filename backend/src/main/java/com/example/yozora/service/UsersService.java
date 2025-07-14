package com.example.yozora.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.yozora.entity.UsersEntity;
import com.example.yozora.form.RegistUserForm;
import com.example.yozora.repository.UsersRepository;
import com.example.yozora.security.LoginUserDetailsService;

@Service
public class UsersService {

    private final UsersRepository usersRepository;
    private final LoginUserDetailsService userDetailsService;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    // コンストラクタ
    public UsersService(LoginUserDetailsService userDetailsService,
            ModelMapper modelMapper,
            UsersRepository usersRepository,
            PasswordEncoder passwordEncoder) {

        this.userDetailsService = userDetailsService;
        this.usersRepository = usersRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    // 新規登録
    public int registUser(RegistUserForm form) throws Exception {

        UsersEntity entity = null;

        entity = convert(form);

        entity.setPassword(passwordEncoder.encode(form.getPassword()));

        int resultRow = usersRepository.userInsert(entity);

        return resultRow;
    }

    // 認証処理（自動ログイン用）
    public void authenticateUser(String email) {
        // ユーザー情報取得
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // 認証トークン作成
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, userDetails.getPassword(), userDetails.getAuthorities());

        // セキュリティコンテキストにセット
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // フォーム→エンティティに型変換用
    private UsersEntity convert(RegistUserForm registUserForm) {

        UsersEntity entity = modelMapper.map(registUserForm, UsersEntity.class);

        return entity;
    }

    // 他のメソッド（例：registUser()）もここに追加される想定
}
