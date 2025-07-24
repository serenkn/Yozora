package com.example.yozora.service;

import com.example.yozora.entity.UsersEntity;
import com.example.yozora.form.UserEditForm;
import com.example.yozora.form.UserRegistForm;
import com.example.yozora.repository.UsersRepository;
import com.example.yozora.security.LoginUserDetailsService;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    public int registUser(UserRegistForm form) {

        UsersEntity entity = convertToEntity(form);

        entity.setPassword(passwordEncoder.encode(form.getPassword()));

        int resultRow = usersRepository.insertUser(entity);

        return resultRow;
    }

    // 認証処理（自動ログイン用）
    public void authenticateUser(String email) {

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, userDetails.getPassword(), userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // メールアドレスからユーザー情報を取得（画面表示や更新用）
    public UsersEntity getUserByEmail(String loginUser) {

        UsersEntity entity = usersRepository.findUserByEmail(loginUser);

        return entity;
    }

    // ユーザ情報の更新
    public int editUser(UserEditForm form) {

        UsersEntity entity = convertToEntity(form);

        // パスワードのエンコード
        entity.setPassword(passwordEncoder.encode(form.getPassword()));

        int resultRow = usersRepository.updateUser(entity);

        return resultRow;

    }

    // ユーザ情報の削除
    public int userDelete(UsersEntity entity) {

        int resultRow = usersRepository.deleteUser(entity);

        return resultRow;

    }

    // フォーム → エンティティに変換：新規登録用
    private UsersEntity convertToEntity(UserRegistForm form) {

        UsersEntity entity = modelMapper.map(form, UsersEntity.class);

        return entity;
    }

    // フォーム → エンティティに変換：プロフィール変更用
    private UsersEntity convertToEntity(UserEditForm form) {

        UsersEntity entity = modelMapper.map(form, UsersEntity.class);

        return entity;
    }
}
