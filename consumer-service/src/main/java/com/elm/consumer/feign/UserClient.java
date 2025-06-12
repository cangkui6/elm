package com.elm.consumer.feign;

import com.elm.common.entity.User;
import com.elm.common.result.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user-service", fallback = UserClientFallback.class)
public interface UserClient {

    @GetMapping("/user/getUserById")
    ResponseResult<User> getUserById(@RequestParam("userId") String userId);

    @PostMapping("/user/getUserById")
    ResponseResult<Integer> checkUserExists(@RequestParam("userId") String userId);

    @PostMapping("/user/getUserByIdByPass")
    ResponseResult<User> loginByIdPassword(@RequestParam("userId") String userId, @RequestParam("password") String password);

    @PostMapping("/user/login")
    ResponseResult<User> login(@RequestParam("userName") String userName, @RequestParam("password") String password);

    @PostMapping("/user/register")
    ResponseResult<Integer> register(@RequestBody User user);
    
    @PostMapping("/user/saveUser")
    ResponseResult<Integer> saveUser(@RequestParam("userId") String userId,
                            @RequestParam("password") String password,
                            @RequestParam("userName") String userName,
                            @RequestParam("userSex") String userSex);
} 