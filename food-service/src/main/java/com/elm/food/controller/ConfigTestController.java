package com.elm.food.controller;

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
@RefreshScope
public class ConfigTestController {

    @Value("${food.service.max-items-per-page:20}")
    private int maxItemsPerPage;
    
    @Value("${food.service.image-upload-path:/uploads/food/}")
    private String imageUploadPath;
    
    @Value("${food.service.max-image-size:5MB}")
    private String maxImageSize;
    
    @Value("${food.service.allowed-image-types:jpg,jpeg,png,gif}")
    private String allowedImageTypes;
    
    @Value("${spring.application.name:food-service}")
    private String applicationName;
    
    @Value("${server.port:9002}")
    private String serverPort;

    @GetMapping("/current-config")
    public Map<String, Object> getCurrentConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("applicationName", applicationName);
        config.put("serverPort", serverPort);
        config.put("maxItemsPerPage", maxItemsPerPage);
        config.put("imageUploadPath", imageUploadPath);
        config.put("maxImageSize", maxImageSize);
        config.put("allowedImageTypes", allowedImageTypes);
        config.put("lastUpdateTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        config.put("message", "食品服务配置支持动态刷新！");
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