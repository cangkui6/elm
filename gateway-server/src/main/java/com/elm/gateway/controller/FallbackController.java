package com.elm.gateway.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
@Slf4j
public class FallbackController {

    @GetMapping("/business-service")
    public ResponseEntity<Map<String, Object>> businessServiceFallback() {
        log.warn("Business service is down, fallback triggered");
        Map<String, Object> result = new HashMap<>();
        result.put("code", 503);
        result.put("message", "呃呃...服务器忙中...");
        result.put("data", null);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(result);
    }

    @GetMapping("/user-service")
    public ResponseEntity<Map<String, Object>> userServiceFallback() {
        log.warn("User service is down, fallback triggered");
        Map<String, Object> result = new HashMap<>();
        result.put("code", 503);
        result.put("message", "呃呃...服务器忙中...");
        result.put("data", null);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(result);
    }

    @GetMapping("/order-service")
    public ResponseEntity<Map<String, Object>> orderServiceFallback() {
        log.warn("Order service is down, fallback triggered");
        Map<String, Object> result = new HashMap<>();
        result.put("code", 503);
        result.put("message", "呃呃...服务器忙中...");
        result.put("data", null);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(result);
    }

    @GetMapping("/food-service")
    public ResponseEntity<Map<String, Object>> foodServiceFallback() {
        log.warn("Food service is down, fallback triggered");
        Map<String, Object> result = new HashMap<>();
        result.put("code", 503);
        result.put("message", "呃呃...服务器忙中...");
        result.put("data", null);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(result);
    }

    @GetMapping("/cart-service")
    public ResponseEntity<Map<String, Object>> cartServiceFallback() {
        log.warn("Cart service is down, fallback triggered");
        Map<String, Object> result = new HashMap<>();
        result.put("code", 503);
        result.put("message", "呃呃...服务器忙中...");
        result.put("data", null);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(result);
    }

    @GetMapping("/address-service")
    public ResponseEntity<Map<String, Object>> addressServiceFallback() {
        log.warn("Address service is down, fallback triggered");
        Map<String, Object> result = new HashMap<>();
        result.put("code", 503);
        result.put("message", "呃呃...服务器忙中...");
        result.put("data", null);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(result);
    }

    @GetMapping("/consumer-service")
    public ResponseEntity<Map<String, Object>> consumerServiceFallback() {
        log.warn("Consumer service is down, fallback triggered");
        Map<String, Object> result = new HashMap<>();
        result.put("code", 503);
        result.put("message", "呃呃...服务器忙中...");
        result.put("data", null);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(result);
    }
} 