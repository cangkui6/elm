package com.elm.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.server.WebFilter;
import org.springframework.core.annotation.Order;

import java.util.Arrays;
import java.util.List;

/**
 * Configuration class for WebFlux to handle SPA routes.
 */
@Configuration
@EnableWebFlux
public class WebFluxConfig implements WebFluxConfigurer {

    @Value("${spa.frontend-url:http://localhost:8081}")
    private String frontendUrl;

    // API paths that should be excluded from frontend routing
    private static final List<String> API_PATHS = Arrays.asList(
        "/business", "/user", "/order", "/food", "/foodCategory", 
        "/deliveryAddress", "/cart", "/api"
    );

    /**
     * WebFilter to forward requests that would normally result in a 404
     * to the frontend application for client-side routing to handle.
     * This filter is given a low order to ensure it runs after all the
     * API routes have been tried.
     */
    @Bean
    @Order(999) // Low priority to ensure API routes are tried first
    public WebFilter notFoundForwardingFilter() {
        return (exchange, chain) -> {
            // 先检查是否是API路径，如果是则直接继续处理链
            String path = exchange.getRequest().getURI().getPath();
            if (isApiPath(path)) {
                return chain.filter(exchange);
            }
            
            // 对于非API路径，处理404错误
            return chain.filter(exchange)
                .onErrorResume(e -> {
                    if (exchange.getResponse().getStatusCode() == HttpStatus.NOT_FOUND) {
                        // 对于非API路径的404，重定向到前端
                        exchange.getResponse().setStatusCode(HttpStatus.FOUND); // 302重定向
                        exchange.getResponse().getHeaders().add("Location", frontendUrl);
                        return exchange.getResponse().setComplete();
                    }
                    return chain.filter(exchange).then();
                });
        };
    }
    
    /**
     * Helper method to determine if a path is an API path.
     */
    private boolean isApiPath(String path) {
        return API_PATHS.stream().anyMatch(path::startsWith);
    }
} 