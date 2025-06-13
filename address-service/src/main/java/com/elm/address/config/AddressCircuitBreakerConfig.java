package com.elm.address.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@Slf4j
public class AddressCircuitBreakerConfig {

    @Bean
    public CircuitBreakerConfig deliveryAddressCircuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
                // 故障率阈值：当故障率达到50%时触发熔断
                .failureRateThreshold(50)
                // 慢调用率阈值：当慢调用率达到80%时触发熔断
                .slowCallRateThreshold(80)
                // 慢调用时间阈值：超过2秒视为慢调用
                .slowCallDurationThreshold(Duration.ofSeconds(2))
                // 最小调用次数：只有达到此次数才会计算故障率
                .minimumNumberOfCalls(5)
                // 滑动窗口类型：COUNT_BASED（基于计数）
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                // 滑动窗口大小
                .slidingWindowSize(10)
                // 半开状态下的测试调用次数
                .permittedNumberOfCallsInHalfOpenState(3)
                // 熔断器开启状态持续时间
                .waitDurationInOpenState(Duration.ofSeconds(10))
                // 是否自动从开启状态转换到半开状态
                .automaticTransitionFromOpenToHalfOpenEnabled(true)
                // 记录的异常类型
                .recordExceptions(Exception.class)
                // 忽略的异常类型
                .ignoreExceptions(IllegalArgumentException.class)
                .build();
    }

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(deliveryAddressCircuitBreakerConfig());
        
        // 注册事件监听器
        registry.getEventPublisher().onEntryAdded(entryAddedEvent -> 
            log.info("CircuitBreaker {} added", entryAddedEvent.getAddedEntry().getName()));
        
        registry.getEventPublisher().onEntryRemoved(entryRemovedEvent -> 
            log.info("CircuitBreaker {} removed", entryRemovedEvent.getRemovedEntry().getName()));
        
        return registry;
    }

    @Bean
    public CircuitBreaker deliveryAddressCircuitBreaker(CircuitBreakerRegistry registry) {
        CircuitBreaker circuitBreaker = registry.circuitBreaker("deliveryAddressService");
        
        // 注册状态转换事件监听器
        circuitBreaker.getEventPublisher()
                .onStateTransition(event -> {
                    log.info("CircuitBreaker state transition: {} -> {} for {}", 
                            event.getStateTransition().getFromState(),
                            event.getStateTransition().getToState(),
                            event.getCircuitBreakerName());
                    
                    // 可以在这里发送告警或者记录到监控系统
                    handleStateTransition(event.getStateTransition().getFromState().name(),
                                         event.getStateTransition().getToState().name());
                });

        // 注册调用成功事件监听器
        circuitBreaker.getEventPublisher()
                .onSuccess(event -> 
                    log.debug("CircuitBreaker call succeeded: duration {}ms", 
                            event.getElapsedDuration().toMillis()));

        // 注册调用失败事件监听器
        circuitBreaker.getEventPublisher()
                .onError(event -> 
                    log.warn("CircuitBreaker call failed: duration {}ms, error: {}", 
                            event.getElapsedDuration().toMillis(), 
                            event.getThrowable().getMessage()));

        // 注册调用被拒绝事件监听器
        circuitBreaker.getEventPublisher()
                .onCallNotPermitted(event -> 
                    log.warn("CircuitBreaker call not permitted for: {}", 
                            event.getCircuitBreakerName()));

        return circuitBreaker;
    }

    private void handleStateTransition(String fromState, String toState) {
        switch (toState) {
            case "OPEN":
                log.error("熔断器已开启，服务进入降级模式");
                // 这里可以发送告警
                break;
            case "HALF_OPEN":
                log.info("熔断器进入半开状态，开始测试服务恢复");
                break;
            case "CLOSED":
                log.info("熔断器已关闭，服务已恢复正常");
                break;
        }
    }
} 