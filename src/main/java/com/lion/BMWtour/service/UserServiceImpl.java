package com.lion.BMWtour.service;

import com.lion.BMWtour.entitiy.User;
import com.lion.BMWtour.repository.UserRepository;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User findByUserId(String userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public boolean isUidDuplicate(String userId) {
        return userRepository.findByUserId(userId).isPresent();
    }

    @Override
    public boolean isNickNameDuplicate(String userNickName) {
        return userRepository.findByUserNickname(userNickName).isPresent();
    }

    @Override
    public void registerUser(User user) {

        userRepository.save(user);
        System.out.println("저장 완료?");
    }

    @Override
    public int login(String userId, String userPw) {
        User user = findByUserId(userId);
        if (user == null) {
            return USER_NOT_EXIST;
        }
        if(BCrypt.checkpw(userPw, user.getUserPw()) ) {
            return CORRECT_LOGIN;
        }
        return WRONG_PASSWORD;
    }

}
