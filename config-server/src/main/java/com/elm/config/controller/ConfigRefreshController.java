package com.elm.config.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.bus.BusProperties;
import org.springframework.cloud.bus.event.RefreshRemoteApplicationEvent;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 配置刷新控制器
 * 提供手动触发配置刷新的API
 */
@Slf4j
@RestController
@RequestMapping("/config-refresh")
public class ConfigRefreshController {

    @Autowired(required = false)
    private ContextRefresher contextRefresher;

    @Autowired(required = false)
    private ApplicationEventPublisher publisher;

    @Autowired(required = false)
    private BusProperties busProperties;

    /**
     * 刷新本地配置
     */
    @PostMapping("/local")
    public Map<String, Object> refreshLocal() {
        Map<String, Object> result = new HashMap<>();
        try {
            if (contextRefresher != null) {
                Set<String> keys = contextRefresher.refresh();
                result.put("success", true);
                result.put("message", "本地配置刷新成功");
                result.put("refreshedKeys", keys);
                result.put("timestamp", System.currentTimeMillis());
                log.info("本地配置刷新成功，刷新的配置项: {}", keys);
            } else {
                result.put("success", false);
                result.put("message", "ContextRefresher未启用");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "本地配置刷新失败: " + e.getMessage());
            log.error("本地配置刷新失败", e);
        }
        return result;
    }

    /**
     * 全局配置刷新（通过消息总线）
     */
    @PostMapping("/global")
    public Map<String, Object> refreshGlobal(@RequestParam(value = "destination", required = false) String destination) {
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
                result.put("timestamp", System.currentTimeMillis());
                log.info("全局配置刷新信号已发送，目标: {}", dest);
            } else {
                result.put("success", false);
                result.put("message", "消息总线未启用，请检查配置");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "全局配置刷新失败: " + e.getMessage());
            log.error("全局配置刷新失败", e);
        }
        return result;
    }

    /**
     * 刷新指定服务的配置
     */
    @PostMapping("/service/{serviceName}")
    public Map<String, Object> refreshService(@PathVariable String serviceName) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (publisher != null && busProperties != null) {
                String destination = serviceName + ":**";
                RefreshRemoteApplicationEvent event = new RefreshRemoteApplicationEvent(
                    this, busProperties.getId(), destination);
                publisher.publishEvent(event);
                result.put("success", true);
                result.put("message", "服务配置刷新信号已发送");
                result.put("serviceName", serviceName);
                result.put("destination", destination);
                result.put("timestamp", System.currentTimeMillis());
                log.info("服务 {} 的配置刷新信号已发送", serviceName);
            } else {
                result.put("success", false);
                result.put("message", "消息总线未启用，请检查配置");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "服务配置刷新失败: " + e.getMessage());
            log.error("服务 {} 配置刷新失败", serviceName, e);
        }
        return result;
    }

    /**
     * 获取配置刷新状态
     */
    @GetMapping("/status")
    public Map<String, Object> getRefreshStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("contextRefresherEnabled", contextRefresher != null);
        status.put("busRefreshEnabled", publisher != null && busProperties != null);
        status.put("configServerName", "config-server");
        status.put("busId", busProperties != null ? busProperties.getId() : null);
        status.put("timestamp", System.currentTimeMillis());
        return status;
    }
} 