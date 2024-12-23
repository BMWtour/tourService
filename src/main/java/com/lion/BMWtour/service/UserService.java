package com.lion.BMWtour.service;

import com.lion.BMWtour.entitiy.User;
import java.util.List;

public interface UserService {

    int CORRECT_LOGIN = 0;
    int WRONG_PASSWORD = 1;
    int USER_NOT_EXIST = 2;

    User findByUid(String user_id);

    List<User> getUsers();

    void registerUser(User user);

    void updateUser(User user);

    void deleteUser(String user_id);

    int login(String user_id, String user_pw);

}
