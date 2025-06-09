package com.elm.user.service;

import com.elm.common.entity.User;

public interface UserService {

    User getUserById(Integer userId);

    User login(String username, String password);

    int register(User user);
} 