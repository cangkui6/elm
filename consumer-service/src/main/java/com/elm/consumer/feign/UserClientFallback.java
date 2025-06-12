package com.elm.consumer.feign;

import com.elm.common.entity.User;
import com.elm.common.result.ResponseResult;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class UserClientFallback implements UserClient {

    @Override
    public ResponseResult<User> getUserById(String userId) {
        return ResponseResult.error("用户服务不可用：获取用户信息失败");
    }

    @Override
    public ResponseResult<Integer> checkUserExists(String userId) {
        return ResponseResult.error("用户服务不可用：检查用户是否存在失败");
    }

    @Override
    public ResponseResult<User> loginByIdPassword(String userId, String password) {
        return ResponseResult.error("用户服务不可用：用户登录失败");
    }

    @Override
    public ResponseResult<User> login(String userName, String password) {
        return ResponseResult.error("用户服务不可用：用户登录失败");
    }

    @Override
    public ResponseResult<Integer> register(User user) {
        return ResponseResult.error("用户服务不可用：用户注册失败");
    }
    
    @Override
    public ResponseResult<Integer> saveUser(String userId, String password, String userName, String userSex) {
        return ResponseResult.error("用户服务不可用：用户注册失败");
    }
} 