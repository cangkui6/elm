package com.elm.address.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class AddressCircuitBreakerInterceptor implements HandlerInterceptor {

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取熔断器
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("deliveryAddressService");
        
        // 检查熔断器状态
        CircuitBreaker.State state = circuitBreaker.getState();
        
        if (state == CircuitBreaker.State.OPEN) {
            // 熔断器开启状态，重定向到降级页面
            String originalUrl = request.getRequestURL().toString();
            if (request.getQueryString() != null) {
                originalUrl += "?" + request.getQueryString();
            }
            
            String fallbackUrl = buildFallbackUrl(originalUrl, "CIRCUIT_BREAKER", "熔断器已开启，服务暂时不可用");
            
            log.warn("Circuit breaker is OPEN, redirecting to fallback page: {}", fallbackUrl);
            response.sendRedirect(fallbackUrl);
            return false;
        } else if (state == CircuitBreaker.State.HALF_OPEN) {
            // 半开状态，记录日志但允许请求通过
            log.info("Circuit breaker is HALF_OPEN, allowing request to test service recovery");
        }
        
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (ex != null) {
            log.error("Request completed with error: {}", ex.getMessage());
            
            // 如果请求失败且响应还未提交，可以重定向到错误页面
            if (!response.isCommitted() && response.getStatus() >= 500) {
                String originalUrl = request.getRequestURL().toString();
                if (request.getQueryString() != null) {
                    originalUrl += "?" + request.getQueryString();
                }
                
                String fallbackUrl = buildFallbackUrl(originalUrl, "SERVICE_ERROR", "服务处理请求时发生错误");
                response.sendRedirect(fallbackUrl);
            }
        }
    }

    private String buildFallbackUrl(String originalUrl, String errorType, String errorMessage) {
        try {
            return String.format("/fallback/address-service?originalUrl=%s&errorType=%s&errorMessage=%s",
                    URLEncoder.encode(originalUrl, StandardCharsets.UTF_8),
                    URLEncoder.encode(errorType, StandardCharsets.UTF_8),
                    URLEncoder.encode(errorMessage, StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("Error building fallback URL", e);
            return "/fallback/address-service";
        }
    }
} 