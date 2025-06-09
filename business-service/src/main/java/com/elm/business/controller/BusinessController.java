package com.elm.business.controller;

import com.elm.business.service.BusinessService;
import com.elm.common.entity.Business;
import com.elm.common.result.ResponseResult;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/business")
@Slf4j
public class BusinessController {

    @Autowired
    private BusinessService businessService;

    @PostMapping("/listBusinessByOrderTypeId")
    @CircuitBreaker(name = "businessService", fallbackMethod = "listBusinessByOrderTypeIdFallback")
    public ResponseResult<List<Business>> listBusinessByOrderTypeId(@RequestParam("orderTypeId") Integer orderTypeId) {
        List<Business> businessList = businessService.listBusinessByOrderTypeId(orderTypeId);
        
        // Enhance with frontend-required fields
        businessList.forEach(business -> {
            business.setRating(4.8);
            business.setOrderCount(4);
            business.setMonthSales(345);
            business.setMinPrice(business.getStarPrice());
            business.setDeliveryMethod("蜂鸟配送");
            business.setDistance("3.22km");
            business.setDeliveryTime("30");
            business.setFoodType(business.getBusinessExplain());
            // Set random badge count for testing
            if (business.getBusinessId() % 3 == 0) {
                business.setBadge(3);
            } else if (business.getBusinessId() % 3 == 1) {
                business.setBadge(2);
            } else {
                business.setBadge(1);
            }
        });
        
        return ResponseResult.success(businessList);
    }

    @PostMapping("/getBusinessById")
    @CircuitBreaker(name = "businessService", fallbackMethod = "getBusinessByIdFallback")
    public ResponseResult<Business> getBusinessById(@RequestParam("businessId") Integer businessId) {
        Business business = businessService.getBusinessById(businessId);
        
        // Enhance with frontend-required fields
        if (business != null) {
            business.setRating(4.8);
            business.setOrderCount(4);
            business.setMonthSales(345);
            business.setMinPrice(business.getStarPrice());
            business.setDeliveryMethod("蜂鸟配送");
            business.setDistance("3.22km");
            business.setDeliveryTime("30");
            business.setFoodType(business.getBusinessExplain());
        }
        
        return ResponseResult.success(business);
    }

    @GetMapping("/listAllBusinesses")
    @CircuitBreaker(name = "businessService", fallbackMethod = "listAllBusinessesFallback")
    public ResponseResult<List<Business>> listAllBusinesses() {
        List<Business> businessList = businessService.listAllBusinesses();
        
        // Enhance with frontend-required fields
        businessList.forEach(business -> {
            business.setRating(4.8);
            business.setOrderCount(4);
            business.setMonthSales(345);
            business.setMinPrice(business.getStarPrice());
            business.setDeliveryMethod("蜂鸟配送");
            business.setDistance("3.22km");
            business.setDeliveryTime("30");
            business.setFoodType(business.getBusinessExplain());
            // Set random badge count
            if (business.getBusinessId() % 3 == 0) {
                business.setBadge(3);
            } else if (business.getBusinessId() % 3 == 1) {
                business.setBadge(2);
            } else {
                business.setBadge(1);
            }
        });
        
        return ResponseResult.success(businessList);
    }

    // Fallback methods
    public ResponseResult<List<Business>> listBusinessByOrderTypeIdFallback(Integer orderTypeId, Throwable t) {
        log.error("Circuit breaker fallback: listBusinessByOrderTypeId failed", t);
        return ResponseResult.error("服务降级：获取商家列表失败");
    }

    public ResponseResult<Business> getBusinessByIdFallback(Integer businessId, Throwable t) {
        log.error("Circuit breaker fallback: getBusinessById failed", t);
        return ResponseResult.error("服务降级：获取商家详情失败");
    }

    public ResponseResult<List<Business>> listAllBusinessesFallback(Throwable t) {
        log.error("Circuit breaker fallback: listAllBusinesses failed", t);
        return ResponseResult.error("服务降级：获取所有商家列表失败");
    }
} 