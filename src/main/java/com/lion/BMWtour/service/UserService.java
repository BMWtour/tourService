package com.lion.BMWtour.service;

import com.lion.BMWtour.dto.request.RegisterUserRequest;
import com.lion.BMWtour.dto.request.UpdateUserRequest;
import com.lion.BMWtour.entity.User;

import java.io.IOException;

public interface UserService {

    int CORRECT_LOGIN = 0;
    int WRONG_PASSWORD = 1;
    int USER_NOT_EXIST = 2;

    User findByUserId(String userId);
    boolean isUidDuplicate(String userId);
    boolean isNickNameDuplicate(String userNickName);

    void registerUser(RegisterUserRequest request)throws IOException;
    void registerUser(User user);

    int login(String userId, String userPw);

    void updateUser(User user);


}
