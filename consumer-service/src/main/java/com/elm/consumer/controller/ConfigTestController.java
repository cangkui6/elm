package com.elm.consumer.controller;

import com.elm.consumer.config.DynamicConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.bus.BusProperties;
import org.springframework.cloud.bus.event.RefreshRemoteApplicationEvent;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 配置测试控制器
 * 用于测试动态配置刷新功能
 */
@Slf4j
@RestController
@RefreshScope
@RequestMapping("/config-test")
public class ConfigTestController {

    @Autowired
    private DynamicConfigProperties dynamicConfig;

    @Autowired(required = false)
    private ContextRefresher contextRefresher;

    @Autowired(required = false)
    private ApplicationEventPublisher publisher;

    @Autowired(required = false)
    private BusProperties busProperties;

    @Value("${consumer.config.customMessage:默认消息}")
    private String customMessage;

    @Value("${consumer.config.debugMode:false}")
    private boolean debugMode;

    @Value("${consumer.config.version:1.0.0}")
    private String version;

    @Value("${spring.application.name}")
    private String applicationName;

    /**
     * 获取当前配置信息
     */
    @GetMapping("/current")
    public Map<String, Object> getCurrentConfig() {
        Map<String, Object> config = new HashMap<>();
        
        // 从配置类获取
        Map<String, Object> dynamicConfigMap = new HashMap<>();
        dynamicConfigMap.put("serviceName", dynamicConfig.getServiceName());
        dynamicConfigMap.put("version", dynamicConfig.getVersion());
        dynamicConfigMap.put("environment", dynamicConfig.getEnvironment());
        dynamicConfigMap.put("debugMode", dynamicConfig.isDebugMode());
        dynamicConfigMap.put("maxRetryCount", dynamicConfig.getMaxRetryCount());
        dynamicConfigMap.put("timeoutMs", dynamicConfig.getTimeoutMs());
        dynamicConfigMap.put("customMessage", dynamicConfig.getCustomMessage());
        dynamicConfigMap.put("featureFlags", dynamicConfig.getFeatureFlags());
        
        config.put("dynamicConfig", dynamicConfigMap);
        
        // 从@Value注解获取
        config.put("valueAnnotations", Map.of(
            "customMessage", customMessage,
            "debugMode", debugMode,
            "version", version,
            "applicationName", applicationName
        ));
        
        // 系统信息
        config.put("systemInfo", Map.of(
            "timestamp", System.currentTimeMillis(),
            "jvmUptime", System.currentTimeMillis() - 
                java.lang.management.ManagementFactory.getRuntimeMXBean().getStartTime()
        ));
        
        log.info("获取当前配置: 版本={}, 调试模式={}, 自定义消息={}", 
            dynamicConfig.getVersion(), dynamicConfig.isDebugMode(), dynamicConfig.getCustomMessage());
        return config;
    }

    /**
     * 测试配置是否生效
     */
    @GetMapping("/test")
    public Map<String, Object> testConfig() {
        Map<String, Object> result = new HashMap<>();
        
        result.put("basicInfo", Map.of(
            "serviceName", dynamicConfig.getServiceName(),
            "version", dynamicConfig.getVersion(),
            "environment", dynamicConfig.getEnvironment(),
            "debugMode", dynamicConfig.isDebugMode()
        ));
        
        result.put("serviceConfig", Map.of(
            "maxRetryCount", dynamicConfig.getMaxRetryCount(),
            "timeoutMs", dynamicConfig.getTimeoutMs(),
            "customMessage", dynamicConfig.getCustomMessage()
        ));
        
        result.put("featureFlags", Map.of(
            "circuitBreakerEnabled", dynamicConfig.getFeatureFlags().isCircuitBreakerEnabled(),
            "cacheEnabled", dynamicConfig.getFeatureFlags().isCacheEnabled(),
            "monitoringEnabled", dynamicConfig.getFeatureFlags().isMonitoringEnabled(),
            "loggingEnabled", dynamicConfig.getFeatureFlags().isLoggingEnabled()
        ));
        
        // 测试功能开关
        if (dynamicConfig.getFeatureFlags().isLoggingEnabled()) {
            log.info("✅ 日志功能已启用");
        }
        
        if (dynamicConfig.getFeatureFlags().isMonitoringEnabled()) {
            log.info("✅ 监控功能已启用");
        }
        
        if (dynamicConfig.getFeatureFlags().isCircuitBreakerEnabled()) {
            log.info("✅ 熔断器功能已启用");
        }
        
        if (dynamicConfig.getFeatureFlags().isCacheEnabled()) {
            log.info("✅ 缓存功能已启用");
        } else {
            log.info("❌ 缓存功能已禁用");
        }
        
        result.put("testResult", Map.of(
            "timestamp", System.currentTimeMillis(),
            "message", "配置测试完成",
            "status", "SUCCESS"
        ));
        
        return result;
    }

    /**
     * 手动刷新本地配置
     */
    @PostMapping("/refresh-local")
    public Map<String, Object> refreshLocalConfig() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            if (contextRefresher != null) {
                Set<String> refreshedKeys = contextRefresher.refresh();
                result.put("success", true);
                result.put("message", "本地配置刷新成功");
                result.put("refreshedKeys", refreshedKeys);
                result.put("refreshedCount", refreshedKeys.size());
                result.put("timestamp", System.currentTimeMillis());
                
                log.info("手动刷新本地配置成功，刷新的配置项: {}", refreshedKeys);
            } else {
                result.put("success", false);
                result.put("message", "ContextRefresher未启用");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "配置刷新失败: " + e.getMessage());
            log.error("手动刷新配置失败", e);
        }
        
        return result;
    }

    /**
     * 触发全局配置刷新（通过消息总线）
     */
    @PostMapping("/refresh-global")
    public Map<String, Object> refreshGlobalConfig(@RequestParam(value = "destination", required = false) String destination) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            if (publisher != null && busProperties != null) {
                String dest = destination != null ? destination : "**";
                RefreshRemoteApplicationEvent event = new RefreshRemoteApplicationEvent(
                    this, busProperties.getId(), dest);
                publisher.publishEvent(event);
                
                result.put("success", true);
                result.put("message", "全局配置刷新信号已发送");
                result.put("destination", dest);
                result.put("originService", busProperties.getId());
                result.put("timestamp", System.currentTimeMillis());
                
                log.info("手动触发全局配置刷新，目标: {}", dest);
            } else {
                result.put("success", false);
                result.put("message", "消息总线未启用，请检查RabbitMQ和Bus配置");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "全局配置刷新失败: " + e.getMessage());
            log.error("手动触发全局配置刷新失败", e);
        }
        
        return result;
    }

    /**
     * 获取配置刷新状态
     */
    @GetMapping("/refresh-status")
    public Map<String, Object> getRefreshStatus() {
        Map<String, Object> status = new HashMap<>();
        
        // 基本信息
        status.put("serviceInfo", Map.of(
            "serviceName", applicationName,
            "configLoaded", dynamicConfig != null,
            "refreshScopeEnabled", true
        ));
        
        // 配置管理状态
        status.put("configManagement", Map.of(
            "contextRefresherEnabled", contextRefresher != null,
            "busRefreshEnabled", publisher != null && busProperties != null,
            "busId", busProperties != null ? busProperties.getId() : null
        ));
        
        // 当前关键配置
        status.put("keyConfigs", Map.of(
            "debugMode", dynamicConfig.isDebugMode(),
            "version", dynamicConfig.getVersion(),
            "customMessage", dynamicConfig.getCustomMessage(),
            "environment", dynamicConfig.getEnvironment(),
            "circuitBreakerEnabled", dynamicConfig.getFeatureFlags().isCircuitBreakerEnabled()
        ));
        
        // 时间戳
        status.put("checkTime", System.currentTimeMillis());
        
        return status;
    }

    /**
     * 健康检查端点
     */
    @GetMapping("/health")
    public Map<String, Object> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        
        try {
            // 检查配置是否正常
            boolean configHealthy = dynamicConfig != null && 
                dynamicConfig.getServiceName() != null && 
                !dynamicConfig.getServiceName().isEmpty();
                
            health.put("status", configHealthy ? "UP" : "DOWN");
            health.put("configService", configHealthy ? "HEALTHY" : "UNHEALTHY");
            health.put("version", dynamicConfig != null ? dynamicConfig.getVersion() : "UNKNOWN");
            health.put("environment", dynamicConfig != null ? dynamicConfig.getEnvironment() : "UNKNOWN");
            health.put("timestamp", System.currentTimeMillis());
            
            // 功能状态检查
            if (dynamicConfig != null) {
                health.put("features", Map.of(
                    "circuitBreaker", dynamicConfig.getFeatureFlags().isCircuitBreakerEnabled() ? "ENABLED" : "DISABLED",
                    "cache", dynamicConfig.getFeatureFlags().isCacheEnabled() ? "ENABLED" : "DISABLED",
                    "monitoring", dynamicConfig.getFeatureFlags().isMonitoringEnabled() ? "ENABLED" : "DISABLED",
                    "logging", dynamicConfig.getFeatureFlags().isLoggingEnabled() ? "ENABLED" : "DISABLED"
                ));
            }
            
        } catch (Exception e) {
            health.put("status", "DOWN");
            health.put("error", e.getMessage());
            log.error("配置健康检查失败", e);
        }
        
        return health;
    }
} 