package com.elm.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;

@SpringBootApplication
@EnableDiscoveryClient
public class GatewayServerApplication {
    
    @Value("${spa.frontend-url:http://localhost:8080}")
    private String frontendUrl;
    
    public static void main(String[] args) {
        SpringApplication.run(GatewayServerApplication.class, args);
    }
    
    /**
     * 在日志中打印前端URL配置，方便调试
     */
    @Bean
    public CommandLineRunner logFrontendUrl() {
        return args -> {
            System.out.println("Frontend URL configured as: " + frontendUrl);
        };
    }
} 