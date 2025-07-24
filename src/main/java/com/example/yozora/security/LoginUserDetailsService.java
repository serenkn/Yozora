package com.example.yozora.security;

import com.example.yozora.entity.UsersEntity;
import com.example.yozora.repository.UsersRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;

@Service
public class LoginUserDetailsService implements UserDetailsService {

    private final UsersRepository usersRepository;

    // コンストラクタ
    public LoginUserDetailsService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    // ログイン時、認証処理
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UsersEntity entity = usersRepository.findUserByEmail(email);

        if (entity == null) {

            throw new UsernameNotFoundException("ユーザーが見つかりませんでした");
        }

        UserDetails userDetails = User.withUsername(entity.getEmail())
                .password(entity.getPassword())
                .authorities(AuthorityUtils.createAuthorityList("ROLE_USER"))
                .build();

        return userDetails;
    }
}
