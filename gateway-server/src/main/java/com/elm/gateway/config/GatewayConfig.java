package com.elm.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("business-service", r -> r.path("/business/**")
                        .uri("lb://business-service"))
                .route("user-service", r -> r.path("/user/**")
                        .uri("lb://user-service"))
                .route("order-service", r -> r.path("/order/**")
                        .uri("lb://order-service"))
                .build();
    }
} 