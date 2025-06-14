package com.elm.consumer.feign;

import com.elm.common.entity.User;
import com.elm.common.result.ResponseResult;
import org.springframework.stereotype.Component;

@Component
public class UserClientFallback implements UserClient {

    @Override
    public ResponseResult<User> getUserById(String userId) {
        return ResponseResult.error("呃呃...服务器忙中...");
    }

    @Override
    public ResponseResult<Integer> checkUserExists(String userId) {
        return ResponseResult.error("呃呃...服务器忙中...");
    }

    @Override
    public ResponseResult<User> loginByIdPassword(String userId, String password) {
        return ResponseResult.error("呃呃...服务器忙中...");
    }

    @Override
    public ResponseResult<User> login(String userName, String password) {
        return ResponseResult.error("呃呃...服务器忙中...");
    }

    @Override
    public ResponseResult<Integer> register(User user) {
        return ResponseResult.error("呃呃...服务器忙中...");
    }
    
    @Override
    public ResponseResult<Integer> saveUser(String userId, String password, String userName, String userSex) {
        return ResponseResult.error("呃呃...服务器忙中...");
    }
} 