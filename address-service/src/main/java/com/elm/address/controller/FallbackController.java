package com.elm.address.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/address-service")
    public String addressServiceFallback(
            @RequestParam(value = "originalUrl", required = false) String originalUrl,
            @RequestParam(value = "errorMessage", required = false, defaultValue = "服务暂时不可用") String errorMessage,
            @RequestParam(value = "errorType", required = false, defaultValue = "CIRCUIT_BREAKER") String errorType,
            @RequestParam(value = "retryAfter", required = false, defaultValue = "30") String retryAfter,
            Model model,
            HttpServletRequest request) {
        
        // 如果没有传入原始URL，尝试从referer获取
        if (originalUrl == null || originalUrl.isEmpty()) {
            originalUrl = request.getHeader("Referer");
            if (originalUrl == null) {
                originalUrl = "/";
            }
        }
        
        // 根据错误类型设置不同的错误信息
        String displayMessage = getErrorMessage(errorType, errorMessage);
        String statusText = getStatusText(errorType);
        
        // 格式化当前时间
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        model.addAttribute("serviceName", "配送地址服务");
        model.addAttribute("errorMessage", displayMessage);
        model.addAttribute("errorType", errorType);
        model.addAttribute("statusText", statusText);
        model.addAttribute("originalUrl", originalUrl);
        model.addAttribute("currentTime", currentTime);
        model.addAttribute("retryAfter", retryAfter);
        model.addAttribute("serverInfo", getServerInfo(request));
        
        return "fallback/address-service-fallback";
    }
    
    private String getErrorMessage(String errorType, String defaultMessage) {
        switch (errorType) {
            case "CIRCUIT_BREAKER":
                return "服务器端由于大量错误请求触发熔断保护机制，暂时停止服务";
            case "TIMEOUT":
                return "服务响应超时，为保护系统稳定性已启动降级处理";
            case "RATE_LIMIT":
                return "当前请求过于频繁，已触发限流保护机制";
            case "SERVICE_UNAVAILABLE":
                return "配送地址服务暂时不可用，请稍后重试";
            default:
                return defaultMessage;
        }
    }
    
    private String getStatusText(String errorType) {
        switch (errorType) {
            case "CIRCUIT_BREAKER":
                return "熔断保护中";
            case "TIMEOUT":
                return "响应超时";
            case "RATE_LIMIT":
                return "限流保护中";
            case "SERVICE_UNAVAILABLE":
                return "服务不可用";
            default:
                return "服务降级中";
        }
    }
    
    private String getServerInfo(HttpServletRequest request) {
        return String.format("服务实例：%s:%d", 
            request.getServerName(), 
            request.getServerPort());
    }
} 