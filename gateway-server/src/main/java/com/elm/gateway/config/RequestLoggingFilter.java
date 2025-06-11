package com.elm.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Filter to log all incoming requests for debugging purposes
 */
@Configuration
public class RequestLoggingFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);
    
    @Bean
    @Order(-1) // Highest priority to ensure it runs first
    public WebFilter logFilter() {
        return (ServerWebExchange exchange, WebFilterChain chain) -> {
            String path = exchange.getRequest().getURI().getPath();
            String method = exchange.getRequest().getMethod().name();
            
            logger.info("Incoming request: {} {}", method, path);
            
            // Add a hook to log the response status
            return chain.filter(exchange)
                .doFinally(signalType -> 
                    logger.info("Response for {} {}: status={}", 
                        method, 
                        path, 
                        exchange.getResponse().getStatusCode())
                );
        };
    }
} 