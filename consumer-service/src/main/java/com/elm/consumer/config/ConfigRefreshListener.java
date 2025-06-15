package com.elm.consumer.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.bus.event.RefreshRemoteApplicationEvent;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 配置刷新监听器
 * 监听配置变更事件并处理
 */
@Slf4j
@Component
public class ConfigRefreshListener {

    @Autowired(required = false)
    private ContextRefresher contextRefresher;

    @Autowired
    private DynamicConfigProperties dynamicConfig;

    /**
     * 监听远程配置刷新事件
     */
    @EventListener
    public void onRefreshRemoteApplicationEvent(RefreshRemoteApplicationEvent event) {
        log.info("=== 接收到远程配置刷新事件 ===");
        log.info("事件源: {}", event.getOriginService());
        log.info("目标服务: {}", event.getDestinationService());
        log.info("事件ID: {}", event.getId());
        
        try {
            if (contextRefresher != null) {
                // 刷新配置
                Set<String> refreshedKeys = contextRefresher.refresh();
                log.info("配置刷新成功，刷新的配置项: {}", refreshedKeys);
                
                // 输出刷新后的关键配置信息
                logKeyConfigAfterRefresh();
            } else {
                log.warn("ContextRefresher未启用，无法刷新配置");
            }
        } catch (Exception e) {
            log.error("配置刷新失败", e);
        }
    }

    /**
     * 监听环境变更事件
     */
    @EventListener
    public void onEnvironmentChangeEvent(EnvironmentChangeEvent event) {
        log.info("=== 环境配置发生变更 ===");
        log.info("变更的配置项数量: {}", event.getKeys().size());
        
        // 详细记录每个变更的配置项
        for (String key : event.getKeys()) {
            log.info("配置项变更: {}", key);
        }
        
        // 输出变更后的关键配置信息
        logKeyConfigAfterRefresh();
    }

    /**
     * 记录关键配置信息
     */
    private void logKeyConfigAfterRefresh() {
        try {
            log.info("=== 配置刷新后的关键配置信息 ===");
            log.info("服务名称: {}", dynamicConfig.getServiceName());
            log.info("版本号: {}", dynamicConfig.getVersion());
            log.info("环境: {}", dynamicConfig.getEnvironment());
            log.info("调试模式: {}", dynamicConfig.isDebugMode());
            log.info("最大重试次数: {}", dynamicConfig.getMaxRetryCount());
            log.info("超时时间: {}ms", dynamicConfig.getTimeoutMs());
            log.info("自定义消息: {}", dynamicConfig.getCustomMessage());
            log.info("熔断器启用: {}", dynamicConfig.getFeatureFlags().isCircuitBreakerEnabled());
            log.info("缓存启用: {}", dynamicConfig.getFeatureFlags().isCacheEnabled());
            log.info("监控启用: {}", dynamicConfig.getFeatureFlags().isMonitoringEnabled());
            log.info("日志启用: {}", dynamicConfig.getFeatureFlags().isLoggingEnabled());
            log.info("=== 配置信息记录完成 ===");
        } catch (Exception e) {
            log.error("记录配置信息时发生错误", e);
        }
    }
}

/**
 * 配置刷新状态管理器
 */
@Slf4j
@Component
class ConfigRefreshStatusManager {
    
    private volatile long lastRefreshTime = 0;
    private volatile int refreshCount = 0;
    private volatile String lastRefreshStatus = "INITIAL";
    
    @EventListener
    public void onRefreshRemoteApplicationEvent(RefreshRemoteApplicationEvent event) {
        updateRefreshStatus("SUCCESS");
    }
    
    @EventListener
    public void onEnvironmentChangeEvent(EnvironmentChangeEvent event) {
        updateRefreshStatus("SUCCESS");
    }
    
    private void updateRefreshStatus(String status) {
        this.lastRefreshTime = System.currentTimeMillis();
        this.refreshCount++;
        this.lastRefreshStatus = status;
        log.info("配置刷新状态更新: 状态={}, 次数={}, 时间={}", status, refreshCount, lastRefreshTime);
    }
    
    public long getLastRefreshTime() {
        return lastRefreshTime;
    }
    
    public int getRefreshCount() {
        return refreshCount;
    }
    
    public String getLastRefreshStatus() {
        return lastRefreshStatus;
    }
} 