package com.example.yozora.service;

import com.example.yozora.entity.UsersEntity;
import com.example.yozora.form.PasswordEditForm;
import com.example.yozora.form.UserEditForm;
import com.example.yozora.form.UserRegistForm;
import com.example.yozora.repository.UsersRepository;
import com.example.yozora.security.LoginUserDetailsService;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UsersService {

    private final UsersRepository usersRepository;
    private final LoginUserDetailsService userDetailsService;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final HttpServletRequest request;

    // コンストラクタ
    public UsersService(LoginUserDetailsService userDetailsService,
            ModelMapper modelMapper,
            UsersRepository usersRepository,
            PasswordEncoder passwordEncoder,
            HttpServletRequest request) {

        this.userDetailsService = userDetailsService;
        this.usersRepository = usersRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.request = request;
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
        // ユーザー情報を取得
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        // 認証情報を作成
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, userDetails.getPassword(), userDetails.getAuthorities());
        // セキュリティコンテキストに認証情報をセット
        SecurityContextHolder.getContext().setAuthentication(authentication);

        request.getSession(true).setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
    }

    // メールアドレスからユーザー情報を取得（画面表示や更新用）
    public UsersEntity getUserByEmail(String loginUser) {

        UsersEntity entity = usersRepository.findUserByEmail(loginUser);

        return entity;
    }

    // ユーザ情報の更新
    public int editUser(UserEditForm form) {

        if (form.getImage() != null && !form.getImage().isEmpty()) {

            String pathToSave = saveImage(form.getImage());

            form.setProfileImage(pathToSave);
        }

        UsersEntity entity = convertToEntity(form);

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

    // エンティティ → フォームに変換：プロフィール変更用
    public UserEditForm convertToForm(UsersEntity entity) {

        UserEditForm form = modelMapper.map(entity, UserEditForm.class);

        return form;
    }

    // プロフィール画像保存処理：MultipartFile → String
    private String saveImage(MultipartFile imageFile) {
        try {
            // 保存ファイル名：UUID + 元ファイル名（重複防止）
            String filename = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();

            // 保存先ディレクトリのパス
            Path path = Paths.get("images/" + filename);

            // ディレクトリがなければ作成
            Files.createDirectories(path.getParent());

            // ファイル保存
            Files.copy(imageFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            // 画像の相対パスを返す
            return "/images/" + filename;

        } catch (IOException e) {
            throw new RuntimeException("画像保存に失敗しました", e);
        }
    }

    // パスワード更新処理
    public boolean updatePassword(String email, PasswordEditForm form) {
        // DBから現在のユーザー情報を取得
        UsersEntity user = usersRepository.findUserByEmail(email);
        // 現在のパスワードが一致しない場合は false を返す
        if (!passwordEncoder.matches(form.getCurrentPassword(), user.getPassword())) {
            return false;
        }
        // 新しいパスワードをハッシュ化してセット
        String hashedNewPassword = passwordEncoder.encode(form.getNewPassword());

        int updated = usersRepository.updatePassword(user.getId(), hashedNewPassword);

        return updated == 1;
    }
}
