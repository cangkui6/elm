package com.elm.business.controller;

import com.elm.business.client.OrderServiceClient;
import com.elm.business.service.BusinessService;
import com.elm.common.entity.Business;
import com.elm.common.entity.Cart;
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
    
    @Autowired
    private OrderServiceClient orderServiceClient;

    @PostMapping("/listBusinessByOrderTypeId")
    @CircuitBreaker(name = "businessService", fallbackMethod = "listBusinessByOrderTypeIdFallback")
    public ResponseResult<List<Business>> listBusinessByOrderTypeId(
            @RequestParam(value = "orderTypeId", required = false) Integer orderTypeId, 
            @RequestParam(value = "categoryId", required = false) Integer categoryId) {
        
        // 打印收到的参数值，方便调试
        log.info("Received orderTypeId: {}, categoryId: {}", orderTypeId, categoryId);
        
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
        } catch (Exception e) {
            log.error("Error querying businesses with orderTypeId: {}", typeId, e);
            return ResponseResult.error("查询商家列表时出错: " + e.getMessage());
        }
    }
    
    @PostMapping("/listBusinessByCategory")
    @CircuitBreaker(name = "businessService", fallbackMethod = "listBusinessByCategoryFallback")
    public ResponseResult<?> listBusinessByCategory(
            @RequestParam("categoryId") Integer categoryId,
            @RequestParam(value = "userId", required = false) Integer userId) {
        
        log.info("Querying businesses with categoryId: {}, userId: {}", categoryId, userId);
        
        try {
            // 使用categoryId查询商家列表
            List<Business> businessList = businessService.listBusinessByOrderTypeId(categoryId);
            
            if (businessList == null || businessList.isEmpty()) {
                log.warn("No businesses found for categoryId: {}", categoryId);
                return ResponseResult.error("没有找到该分类的商家");
            }
            
            log.info("Found {} businesses for categoryId: {}", businessList.size(), categoryId);
            
            // 增强商家信息
            businessList.forEach(business -> {
                business.setRating(4.8);
                business.setOrderCount(4);
                business.setMonthSales(345);
                business.setMinPrice(business.getStarPrice());
                business.setDeliveryMethod("蜂鸟配送");
                business.setDistance("3.22km");
                business.setDeliveryTime("30");
                business.setFoodType(business.getBusinessExplain());
                if (business.getBusinessId() % 3 == 0) {
                    business.setBadge(3);
                } else if (business.getBusinessId() % 3 == 1) {
                    business.setBadge(2);
                } else {
                    business.setBadge(1);
                }
            });
            
            // 如果用户已登录，查询购物车信息
            if (userId != null) {
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("businessList", businessList);
                
                // 查询该用户的购物车信息
                ResponseResult<List<Cart>> cartResponse = orderServiceClient.listCart(userId, null);
                
                if (cartResponse.getCode() == 1) {
                    List<Cart> cartList = cartResponse.getData();
                    
                    // 统计每个商家的购物车商品数量
                    Map<Integer, Integer> businessCartCount = new HashMap<>();
                    if (cartList != null && !cartList.isEmpty()) {
                        for (Cart cart : cartList) {
                            Integer businessId = cart.getBusinessId();
                            businessCartCount.put(
                                businessId, 
                                businessCartCount.getOrDefault(businessId, 0) + cart.getQuantity()
                            );
                        }
                    }
                    
                    responseMap.put("businessCartCount", businessCartCount);
                }
                
                return ResponseResult.success(responseMap);
            }
            
            return ResponseResult.success(businessList);
            
        } catch (Exception e) {
            log.error("Error querying businesses with categoryId: {}", categoryId, e);
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
            
            // Enhance with frontend-required fields
            business.setRating(4.8);
            business.setOrderCount(4);
            business.setMonthSales(345);
            business.setMinPrice(business.getStarPrice());
            business.setDeliveryMethod("蜂鸟配送");
            business.setDistance("3.22km");
            business.setDeliveryTime("30");
            business.setFoodType(business.getBusinessExplain());
            
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
    public ResponseResult<List<Business>> listBusinessByOrderTypeIdFallback(Integer orderTypeId, Integer categoryId, Throwable t) {
        log.error("Circuit breaker fallback: listBusinessByOrderTypeId failed", t);
        return ResponseResult.error("服务降级：获取商家列表失败");
    }
    
    public ResponseResult<?> listBusinessByCategoryFallback(Integer categoryId, Integer userId, Throwable t) {
        log.error("Circuit breaker fallback: listBusinessByCategory failed", t);
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