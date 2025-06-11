package com.elm.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

@Configuration
public class CustomErrorController {
    
    @Value("${spa.frontend-url:http://localhost:8080}")
    private String frontendUrl;
    
    // API paths that should not be redirected to the frontend
    private static final List<String> API_PATHS = Arrays.asList(
        "/business", "/user", "/order", "/food", "/foodCategory", 
        "/deliveryAddress", "/cart", "/api"
    );
    
    /**
     * Custom exception handler to handle 404 errors
     */
    @Bean
    @Order(-2) // High precedence
    public ErrorWebExceptionHandler errorWebExceptionHandler() {
        return (ServerWebExchange exchange, Throwable ex) -> {
            // 对于API路径，让服务自己处理错误
            String path = exchange.getRequest().getURI().getPath();
            boolean isApiPath = isApiPath(path);
            
            if (ex instanceof ResponseStatusException) {
                ResponseStatusException responseStatusException = (ResponseStatusException) ex;
                
                // 对于404错误
                if (responseStatusException.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                    // 如果是API路径，返回JSON格式的404错误
                    if (isApiPath) {
                        exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
                        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                        // 简单返回错误，不捕获API服务的错误
                        return Mono.error(ex);
                    } else {
                        // 非API路径，重定向到前端应用
                        exchange.getResponse().setStatusCode(HttpStatus.FOUND);
                        exchange.getResponse().getHeaders().setLocation(URI.create(frontendUrl));
                        return exchange.getResponse().setComplete();
                    }
                }
            }
            
            // 对于其他错误，让默认处理器处理
            return Mono.error(ex);
        };
    }
    
    /**
     * 判断一个路径是否是API路径
     */
    private boolean isApiPath(String path) {
        return API_PATHS.stream().anyMatch(path::startsWith);
    }
    
    // Simple error response class
    private static class ErrorResponse {
        private final String error;
        private final String message;
        
        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }
        
        public String getError() {
            return error;
        }
        
        public String getMessage() {
            return message;
        }
    }
} 