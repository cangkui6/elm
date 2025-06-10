package com.elm.user.service;

import com.elm.common.entity.User;

public interface UserService {

    User getUserById(String userId);

    User login(String userName, String password);

    int register(User user);

    User loginByIdPassword(String userId, String password);
} 