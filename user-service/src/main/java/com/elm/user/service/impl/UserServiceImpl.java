package com.elm.user.service.impl;

import com.elm.common.entity.User;
import com.elm.user.mapper.UserMapper;
import com.elm.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User getUserById(String userId) {
        return userMapper.getUserById(userId);
    }

    @Override
    public User login(String username, String password) {
        return userMapper.getUserByUsernameAndPassword(username, password);
    }

    @Override
    public User loginByIdPassword(String userId, String password) {
        return userMapper.getUserByIdAndPassword(userId, password);
    }

    @Override
    public int register(User user) {
        return userMapper.saveUser(user);
    }
} 