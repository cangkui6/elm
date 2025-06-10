package com.elm.order.controller;

import com.elm.common.entity.Business;
import com.elm.common.result.ResponseResult;
import com.elm.order.mapper.BusinessMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/business")
@Slf4j
public class BusinessController {

    @Autowired
    private BusinessMapper businessMapper;

    /**
     * 根据商家ID获取商家信息
     * @param businessId 商家ID
     * @return 商家信息
     */
    @PostMapping("/getBusinessById")
    @CircuitBreaker(name = "businessService", fallbackMethod = "getBusinessByIdFallback")
    public ResponseResult<Business> getBusinessById(@RequestParam("businessId") Integer businessId) {
        Business business = businessMapper.getBusinessById(businessId);
        if (business != null) {
            return ResponseResult.success(business);
        } else {
            return ResponseResult.error("商家不存在");
        }
    }

    // Fallback methods
    public ResponseResult<Business> getBusinessByIdFallback(Integer businessId, Throwable t) {
        log.error("Circuit breaker fallback: getBusinessById failed", t);
        return ResponseResult.error("服务降级：获取商家信息失败");
    }
} 