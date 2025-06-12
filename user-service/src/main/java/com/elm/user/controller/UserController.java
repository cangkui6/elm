package com.elm.user.controller;

import com.elm.common.entity.User;
import com.elm.common.result.ResponseResult;
import com.elm.user.service.UserService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
    
    @Value("${server.port}")
    private String serverPort;

    @GetMapping("/instance-info")
    public ResponseResult<Map<String, Object>> getInstanceInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("port", serverPort);
        info.put("timestamp", System.currentTimeMillis());
        info.put("instance", "user-service-" + serverPort);
        log.info("Instance info requested on port: {}", serverPort);
        return ResponseResult.success(info);
    }

    @GetMapping("/getUserById")
    @CircuitBreaker(name = "userService", fallbackMethod = "getUserByIdFallback")
    public ResponseResult<User> getUserById(@RequestParam("userId") String userId) {
        User user = userService.getUserById(userId);
        log.info("获取用户信息请求处理 [端口:{}]: userId={}", serverPort, userId);
        return ResponseResult.success(user);
    }

    // 适配前端登录页面所需的API
    @PostMapping("/getUserById")
    @CircuitBreaker(name = "userService", fallbackMethod = "userExistsFallback")
    public ResponseResult<Integer> checkUserExists(@RequestParam("userId") String userId) {
        log.info("检查用户是否存在 [端口:{}]: userId={}", serverPort, userId);
        User user = userService.getUserById(userId);
        if (user != null) {
            return ResponseResult.success(1);  // 用户存在返回1
        } else {
            return ResponseResult.success(0);  // 用户不存在返回0
        }
    }

    // 适配前端登录页面所需的API
    @PostMapping("/getUserByIdByPass")
    @CircuitBreaker(name = "userService", fallbackMethod = "loginByIdPasswordFallback")
    public ResponseResult<User> loginByIdPassword(@RequestParam("userId") String userId, 
                                         @RequestParam("password") String password) {
        log.info("使用用户ID和密码登录 [端口:{}]: userId={}", serverPort, userId);
        User user = userService.loginByIdPassword(userId, password);
        if (user != null) {
            return ResponseResult.success(user);
        } else {
            return ResponseResult.error("用户名或密码错误");
        }
    }

    @PostMapping("/login")
    @CircuitBreaker(name = "userService", fallbackMethod = "loginFallback")
    public ResponseResult<User> login(@RequestParam("userName") String userName, @RequestParam("password") String password) {
        User user = userService.login(userName, password);
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
    
    // 适配前端注册页面所需的API
    @PostMapping("/saveUser")
    @CircuitBreaker(name = "userService", fallbackMethod = "saveUserFallback")
    public ResponseResult<Integer> saveUser(@RequestParam("userId") String userId,
                                   @RequestParam("password") String password,
                                   @RequestParam("userName") String userName,
                                   @RequestParam("userSex") String userSex) {
        log.info("保存新用户 [端口:{}]: userId={}", serverPort, userId);
        
        try {
            User user = new User();
            user.setUserId(userId);
            user.setPassword(password);
            user.setUserName(userName);
            user.setUserSex(Integer.parseInt(userSex));
            user.setDelTag(1); // 正常状态
            
            int result = userService.register(user);
            if (result > 0) {
                return ResponseResult.success(1); // 成功返回1
            } else {
                return ResponseResult.error("注册失败");
            }
        } catch (Exception e) {
            log.error("注册用户时发生错误: {}", e.getMessage(), e);
            return ResponseResult.error("注册失败: " + e.getMessage());
        }
    }

    // Fallback methods
    public ResponseResult<User> getUserByIdFallback(String userId, Throwable t) {
        log.error("Circuit breaker fallback: getUserById failed", t);
        return ResponseResult.error("服务降级：获取用户信息失败");
    }

    public ResponseResult<Integer> userExistsFallback(String userId, Throwable t) {
        log.error("Circuit breaker fallback: checkUserExists failed", t);
        return ResponseResult.error("服务降级：检查用户是否存在失败");
    }
    
    public ResponseResult<User> loginByIdPasswordFallback(String userId, String password, Throwable t) {
        log.error("Circuit breaker fallback: loginByIdPassword failed", t);
        return ResponseResult.error("服务降级：使用ID和密码登录失败");
    }
    
    public ResponseResult<Integer> saveUserFallback(String userId, String password, String userName, String userSex, Throwable t) {
        log.error("Circuit breaker fallback: saveUser failed", t);
        return ResponseResult.error("服务降级：保存用户失败");
    }

    public ResponseResult<User> loginFallback(String userName, String password, Throwable t) {
        log.error("Circuit breaker fallback: login failed", t);
        return ResponseResult.error("服务降级：登录失败");
    }

    public ResponseResult<Integer> registerFallback(User user, Throwable t) {
        log.error("Circuit breaker fallback: register failed", t);
        return ResponseResult.error("服务降级：注册失败");
    }
} 