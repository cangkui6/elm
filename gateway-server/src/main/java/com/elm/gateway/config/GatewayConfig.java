package com.elm.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;

import java.util.Arrays;

@Configuration
public class GatewayConfig {

    /**
     * Define routes programmatically to ensure they have higher priority than the fallback route
     * 注释掉此配置，避免与application.yml中的配置冲突
     */
    /*
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("business-service-route", r -> r.path("/business/**", "/food/**", "/foodCategory/**")
                        .uri("lb://business-service"))
                .route("user-service-route", r -> r.path("/user/**", "/deliveryAddress/**")
                        .uri("lb://user-service"))
                .route("order-service-route", r -> r.path("/order/**", "/cart/**")
                        .uri("lb://order-service"))
                .build();
    }
    */

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(Arrays.asList("http://localhost:8080", "http://localhost:8081"));
        corsConfig.setMaxAge(3600L);
        corsConfig.addAllowedMethod("*");
        corsConfig.addAllowedHeader("*");
        corsConfig.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
} 