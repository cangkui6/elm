package com.elm.business.client;

import feign.Logger;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    
    private final ObjectFactory<HttpMessageConverters> messageConverters;
    
    public FeignConfig(ObjectFactory<HttpMessageConverters> messageConverters) {
        this.messageConverters = messageConverters;
    }

    @Bean
    public Encoder feignEncoder() {
        return new SpringFormEncoder(new SpringEncoder(messageConverters));
    }
    
    @Bean
    public Decoder feignDecoder() {
        return new SpringDecoder(messageConverters);
    }
    
    @Bean
    Logger.Level feignLoggerLevel() {
        // 设置日志级别为FULL，打印所有请求和响应的细节
        return Logger.Level.FULL;
    }
} 