package com.lion.BMWtour.service;

import com.lion.BMWtour.entitiy.User;
import com.lion.BMWtour.repository.UserRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User findByUid(String user_id) {
        return null;
    }

    @Override
    public List<User> getUsers() {
        return List.of();
    }

    @Override
    public void registerUser(User user) {

    }

    @Override
    public void updateUser(User user) {

    }

    @Override
    public void deleteUser(String user_id) {

    }

    @Override
    public int login(String user_id, String user_pw) {
        return 0;
    }
}
