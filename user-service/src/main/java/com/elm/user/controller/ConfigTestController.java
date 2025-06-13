package com.elm.user.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/config-test")
@RefreshScope // 这个注解很重要，使得这个Bean能够接收配置刷新
public class ConfigTestController {

    @Value("${user.service.max-login-attempts:5}")
    private int maxLoginAttempts;
    
    @Value("${user.service.token-expire-time:3600}")
    private int tokenExpireTime;
    
    @Value("${user.service.password-min-length:6}")
    private int passwordMinLength;
    
    @Value("${spring.application.name:user-service}")
    private String applicationName;
    
    @Value("${server.port:9001}")
    private String serverPort;

    @GetMapping("/current-config")
    public Map<String, Object> getCurrentConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("applicationName", applicationName);
        config.put("serverPort", serverPort);
        config.put("maxLoginAttempts", maxLoginAttempts);
        config.put("tokenExpireTime", tokenExpireTime);
        config.put("passwordMinLength", passwordMinLength);
        config.put("lastUpdateTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        config.put("message", "这些配置可以通过配置中心动态刷新！");
        return config;
    }

    @GetMapping("/refresh-status")
    public Map<String, Object> getRefreshStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("serviceName", applicationName);
        status.put("port", serverPort);
        status.put("configCenter", "已连接到配置中心");
        status.put("busEnabled", "消息总线已启用");
        status.put("refreshScope", "支持动态刷新");
        status.put("currentTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return status;
    }
} 