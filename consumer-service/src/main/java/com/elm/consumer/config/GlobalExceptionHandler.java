package com.elm.consumer.config;

import com.elm.common.result.ResponseResult;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.concurrent.TimeoutException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理熔断器异常
     */
    @ExceptionHandler(CallNotPermittedException.class)
    public ResponseEntity<ResponseResult<String>> handleCircuitBreakerException(CallNotPermittedException e) {
        log.error("熔断器已开启，服务暂时不可用: {}", e.getMessage());
        ResponseResult<String> result = ResponseResult.error(503, "呃呃...服务器忙中...");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(result);
    }

    /**
     * 处理Feign调用异常
     */
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ResponseResult<String>> handleFeignException(FeignException e) {
        log.error("Feign调用异常: status={}, message={}", e.status(), e.getMessage());
        
        // status=-1通常表示连接拒绝，这是服务不可用的表现
        ResponseResult<String> result = ResponseResult.error(503, "呃呃...服务器忙中...");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(result);
    }

    /**
     * 处理超时异常
     */
    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<ResponseResult<String>> handleTimeoutException(TimeoutException e) {
        log.error("请求超时: {}", e.getMessage());
        ResponseResult<String> result = ResponseResult.error(503, "呃呃...服务器忙中...");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(result);
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseResult<String>> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常: {}", e.getMessage(), e);
        
        // 检查是否是熔断相关的异常
        if (e.getMessage() != null && 
            (e.getMessage().contains("Circuit breaker") || 
             e.getMessage().contains("服务降级") ||
             e.getMessage().contains("熔断") ||
             e.getMessage().contains("Connection refused"))) {
            ResponseResult<String> result = ResponseResult.error(503, "呃呃...服务器忙中...");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(result);
        }
        
        ResponseResult<String> result = ResponseResult.error(500, "服务器内部错误");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    /**
     * 处理通用异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseResult<String>> handleException(Exception e) {
        log.error("系统异常: {}", e.getMessage(), e);
        ResponseResult<String> result = ResponseResult.error(500, "系统异常");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }
} 