package com.elm.business.controller;

import com.elm.business.service.BusinessService;
import com.elm.common.entity.Business;
import com.elm.common.result.ResponseResult;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/business")
@Slf4j
public class BusinessController {

    @Autowired
    private BusinessService businessService;

    // 通用方法判断响应是否成功
    private boolean isResponseSuccessful(ResponseResult<?> response) {
        return response != null && 
               (response.getCode() == 1 || response.getCode() == 200) && 
               response.getData() != null;
    }

    @PostMapping("/listBusinessByOrderTypeId")
    @CircuitBreaker(name = "businessService", fallbackMethod = "listBusinessByOrderTypeIdFallback")
    public ResponseResult<List<Business>> listBusinessByOrderTypeId(
            @RequestParam(value = "orderTypeId", required = false) Integer orderTypeId, 
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "userId", required = false) String userId) {
        
        // 打印收到的参数值，方便调试
        log.info("Received orderTypeId: {}, categoryId: {}, userId: {}", orderTypeId, categoryId, userId);
        
        // 优先使用orderTypeId，如果为null则使用categoryId
        Integer typeId = orderTypeId != null ? orderTypeId : categoryId;
        
        // 如果两个参数都为null，返回所有商家
        if (typeId == null) {
            log.warn("Both orderTypeId and categoryId are null, returning all businesses");
            return ResponseResult.success(businessService.listAllBusinesses());
        }
        
        log.info("Querying businesses with orderTypeId: {}", typeId);
        try {
            List<Business> businessList = businessService.listBusinessByOrderTypeId(typeId);
            
            if (businessList == null || businessList.isEmpty()) {
                log.warn("No businesses found for orderTypeId: {}", typeId);
                return ResponseResult.error("没有找到该分类的商家");
            }
            
            log.info("Found {} businesses for orderTypeId: {}", businessList.size(), typeId);
            
            // 设置基本字段
            businessList.forEach(business -> {
                business.setMinPrice(business.getStarPrice());
            });
            
            return ResponseResult.success(businessList);
        } catch (Exception e) {
            log.error("Error querying businesses with orderTypeId: {}", typeId, e);
            return ResponseResult.error("查询商家列表时出错: " + e.getMessage());
        }
    }

    @PostMapping("/getBusinessById")
    @CircuitBreaker(name = "businessService", fallbackMethod = "getBusinessByIdFallback")
    public ResponseResult<Business> getBusinessById(@RequestParam("businessId") Integer businessId) {
        try {
            log.info("Querying business with businessId: {}", businessId);
            Business business = businessService.getBusinessById(businessId);
            
            if (business == null) {
                log.warn("No business found for businessId: {}", businessId);
                return ResponseResult.error("没有找到该商家");
            }
            
            // 仅设置必要字段
            business.setMinPrice(business.getStarPrice());
            
            return ResponseResult.success(business);
        } catch (Exception e) {
            log.error("Error querying business with businessId: {}", businessId, e);
            return ResponseResult.error("查询商家详情时出错: " + e.getMessage());
        }
    }

    @GetMapping("/listAllBusinesses")
    @CircuitBreaker(name = "businessService", fallbackMethod = "listAllBusinessesFallback")
    public ResponseResult<List<Business>> listAllBusinesses() {
        List<Business> businessList = businessService.listAllBusinesses();
        
        // 仅初始化必要字段
        businessList.forEach(business -> {
            business.setMinPrice(business.getStarPrice());
        });
        
        return ResponseResult.success(businessList);
    }

    /**
     * 查询数据库中所有商家信息
     */
    @GetMapping("/getAllBusinessesFromDb")
    public ResponseResult<?> getAllBusinessesFromDb() {
        try {
            // 查询数据库中所有商家
            List<Business> businessList = businessService.listAllBusinesses();
            
            // 统计每个分类下的商家数量
            Map<Integer, Integer> categoryCountMap = new HashMap<>();
            for (Business business : businessList) {
                Integer typeId = business.getOrderTypeId();
                categoryCountMap.put(typeId, categoryCountMap.getOrDefault(typeId, 0) + 1);
            }
            
            // 创建响应
            Map<String, Object> result = new HashMap<>();
            result.put("totalCount", businessList.size());
            result.put("businessList", businessList);
            result.put("categoryCountMap", categoryCountMap);
            
            return ResponseResult.success(result);
        } catch (Exception e) {
            log.error("查询所有商家失败", e);
            return ResponseResult.error("查询所有商家失败: " + e.getMessage());
        }
    }

    // Fallback methods
    public ResponseResult<List<Business>> listBusinessByOrderTypeIdFallback(Integer orderTypeId, Integer categoryId, String userId, Throwable t) {
        log.error("Circuit breaker fallback: listBusinessByOrderTypeId failed", t);
        return ResponseResult.error("服务降级：获取商家列表失败");
    }
    
    public ResponseResult<Business> getBusinessByIdFallback(Integer businessId, Throwable t) {
        log.error("Circuit breaker fallback: getBusinessById failed", t);
        return ResponseResult.error("服务降级：获取商家详情失败");
    }
    
    public ResponseResult<List<Business>> listAllBusinessesFallback(Throwable t) {
        log.error("Circuit breaker fallback: listAllBusinesses failed", t);
        return ResponseResult.error("服务降级：获取商家列表失败");
    }
} 