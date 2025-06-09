package com.elm.user.controller;

import com.elm.common.entity.User;
import com.elm.common.result.ResponseResult;
import com.elm.user.service.UserService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/getUserById")
    @CircuitBreaker(name = "userService", fallbackMethod = "getUserByIdFallback")
    public ResponseResult<User> getUserById(@RequestParam("userId") Integer userId) {
        User user = userService.getUserById(userId);
        return ResponseResult.success(user);
    }

    @PostMapping("/login")
    @CircuitBreaker(name = "userService", fallbackMethod = "loginFallback")
    public ResponseResult<User> login(@RequestParam("username") String username, @RequestParam("password") String password) {
        User user = userService.login(username, password);
        if (user != null) {
            return ResponseResult.success(user);
        } else {
            return ResponseResult.error("用户名或密码错误");
        }
    }

    @PostMapping("/register")
    @CircuitBreaker(name = "userService", fallbackMethod = "registerFallback")
    public ResponseResult<Integer> register(@RequestBody User user) {
        int result = userService.register(user);
        if (result > 0) {
            return ResponseResult.success(result);
        } else {
            return ResponseResult.error("注册失败");
        }
    }

    // Fallback methods
    public ResponseResult<User> getUserByIdFallback(Integer userId, Throwable t) {
        log.error("Circuit breaker fallback: getUserById failed", t);
        return ResponseResult.error("服务降级：获取用户信息失败");
    }

    public ResponseResult<User> loginFallback(String username, String password, Throwable t) {
        log.error("Circuit breaker fallback: login failed", t);
        return ResponseResult.error("服务降级：登录失败");
    }

    public ResponseResult<Integer> registerFallback(User user, Throwable t) {
        log.error("Circuit breaker fallback: register failed", t);
        return ResponseResult.error("服务降级：注册失败");
    }
} 