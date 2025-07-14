package com.example.yozora.security;

import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;

import com.example.yozora.entity.UsersEntity;
import com.example.yozora.repository.UsersRepository;

@Service
public class LoginUserDetailsService implements UserDetailsService {

    private final UsersRepository usersRepository;
    private final ModelMapper modelMapper;

    // コンストラクタ
    public LoginUserDetailsService(UsersRepository usersRepository, ModelMapper modelMapper) {
        this.usersRepository = usersRepository;
        this.modelMapper = modelMapper;
    }

    // メアドで検索し、認証付与 ログイン、新規登録
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Map<String, Object> resultMap = null;

        try {
            resultMap = usersRepository.findUserByEmail(email);
        } catch (Exception e) {
            throw new UsernameNotFoundException("ユーザー取得時にエラーが発生しました");
        }

        if (resultMap == null || resultMap.isEmpty()) {
            throw new UsernameNotFoundException("ユーザーが見つかりませんでした");
        }

        UsersEntity entity = modelMapper.map(resultMap, UsersEntity.class);

        UserDetails userDetails = User.withUsername(entity.getEmail())
                .password(entity.getPassword())
                .authorities(AuthorityUtils.createAuthorityList("ROLE_USER"))
                .build();

        return userDetails;
    }
}
