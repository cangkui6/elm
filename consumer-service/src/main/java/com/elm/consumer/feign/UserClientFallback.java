package com.elm.consumer.feign;

import com.elm.common.entity.User;
import com.elm.common.result.ResponseResult;

public class UserClientFallback implements UserClient {

    @Override
    public ResponseResult<User> getUserById(String userId) {
        return ResponseResult.error("用户服務不可用：獲取用戶信息失敗");
    }

    @Override
    public ResponseResult<Integer> checkUserExists(String userId) {
        return ResponseResult.error("用户服務不可用：檢查用戶是否存在失敗");
    }

    @Override
    public ResponseResult<User> loginByIdPassword(String userId, String password) {
        return ResponseResult.error("用户服務不可用：用戶登錄失敗");
    }

    @Override
    public ResponseResult<User> login(String userName, String password) {
        return ResponseResult.error("用户服務不可用：用戶登錄失敗");
    }

    @Override
    public ResponseResult<Integer> register(User user) {
        return ResponseResult.error("用户服務不可用：用戶註冊失敗");
    }
    
    @Override
    public ResponseResult<Integer> saveUser(String userId, String password, String userName, String userSex) {
        return ResponseResult.error("用户服務不可用：用戶註冊失敗");
    }
} 