package com.elm.consumer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * 动态配置属性
 * 使用@RefreshScope注解支持配置动态刷新
 */
@Data
@Component
@RefreshScope
@ConfigurationProperties(prefix = "consumer.config")
public class DynamicConfigProperties {

    /**
     * 服务名称
     */
    private String serviceName = "consumer-service";

    /**
     * 版本号
     */
    private String version = "1.0.0";

    /**
     * 环境
     */
    private String environment = "dev";

    /**
     * 是否启用调试模式
     */
    private boolean debugMode = false;

    /**
     * 最大重试次数
     */
    private int maxRetryCount = 3;

    /**
     * 超时时间（毫秒）
     */
    private long timeoutMs = 5000;

    /**
     * 自定义消息
     */
    private String customMessage = "欢迎使用饿了么系统";

    /**
     * 功能开关
     */
    private FeatureFlags featureFlags = new FeatureFlags();

    @Data
    public static class FeatureFlags {
        /**
         * 是否启用熔断功能
         */
        private boolean circuitBreakerEnabled = true;

        /**
         * 是否启用缓存
         */
        private boolean cacheEnabled = true;

        /**
         * 是否启用监控
         */
        private boolean monitoringEnabled = true;

        /**
         * 是否启用日志记录
         */
        private boolean loggingEnabled = true;
    }
} 