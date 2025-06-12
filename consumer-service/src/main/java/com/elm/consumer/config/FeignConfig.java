package com.elm.consumer.config;

import feign.Logger;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@LoadBalancerClients({
    @LoadBalancerClient(name = "business-service", configuration = LoadBalancerConfig.class),
    @LoadBalancerClient(name = "user-service", configuration = LoadBalancerConfig.class),
    @LoadBalancerClient(name = "order-service", configuration = LoadBalancerConfig.class),
    @LoadBalancerClient(name = "food-service", configuration = LoadBalancerConfig.class),
    @LoadBalancerClient(name = "cart-service", configuration = LoadBalancerConfig.class),
    @LoadBalancerClient(name = "address-service", configuration = LoadBalancerConfig.class)
})
public class FeignConfig {

    /**
     * 配置Feign日志级别
     */
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
    
    /**
     * 配置负载均衡的RestTemplate
     */
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
} 