package com.elm.address.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AddressCircuitBreakerInterceptor circuitBreakerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加拦截器来处理熔断器状态
        registry.addInterceptor(circuitBreakerInterceptor)
                .addPathPatterns("/deliveryAddress/**");
    }
} 