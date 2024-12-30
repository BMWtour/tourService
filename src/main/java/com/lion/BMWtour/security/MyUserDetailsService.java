package com.lion.BMWtour.security;

import com.lion.BMWtour.entity.User;
import com.lion.BMWtour.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
@Service
@Slf4j
public class MyUserDetailsService implements UserDetailsService {
    @Autowired private UserService userService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByUserId(username);
        if (user == null) {
            log.warn("로그인 실패: 아이디가 존제하지 않습니다 (username: " + username + ")");
            throw new UsernameNotFoundException("아이디가 존재하지 않습니다");
        }
        log.info("Login 시도: " + user.getUserId());
        return new MyUserDetails(user);
    }
}