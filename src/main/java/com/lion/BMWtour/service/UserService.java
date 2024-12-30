package com.lion.BMWtour.service;

import com.lion.BMWtour.entity.User;

public interface UserService {

    int CORRECT_LOGIN = 0;
    int WRONG_PASSWORD = 1;
    int USER_NOT_EXIST = 2;

    User findByUserId(String userId);
    boolean isUidDuplicate(String userId);
    boolean isNickNameDuplicate(String userNickName);

    void registerUser(User user);

    int login(String userId, String userPw);

}
