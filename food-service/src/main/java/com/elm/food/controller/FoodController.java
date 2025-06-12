package com.elm.food.controller;

import com.elm.food.service.FoodService;
import com.elm.common.entity.Food;
import com.elm.common.result.ResponseResult;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/food")
@Slf4j
public class FoodController {

    @Autowired
    private FoodService foodService;

    @PostMapping("/listFoodByBusinessId")
    @CircuitBreaker(name = "foodService", fallbackMethod = "listFoodByBusinessIdFallback")
    public ResponseResult<List<Food>> listFoodByBusinessId(@RequestParam("businessId") Integer businessId) {
        try {
            log.info("Querying foods for businessId: {}", businessId);
            List<Food> foodList = foodService.listFoodByBusinessId(businessId);
            
            if (foodList == null || foodList.isEmpty()) {
                log.warn("No foods found for businessId: {}", businessId);
                return ResponseResult.error("该商家暂无食品");
            }
            
            log.info("Found {} foods for businessId: {}", foodList.size(), businessId);
            
            // Enhance with frontend-required fields
            foodList.forEach(food -> {
                food.setDescription(food.getFoodExplain());
                food.setPrice(food.getFoodPrice());
                food.setQuantity(0);
            });
            
            return ResponseResult.success(foodList);
        } catch (Exception e) {
            log.error("Error querying foods for businessId: {}", businessId, e);
            return ResponseResult.error("查询食品列表时出错: " + e.getMessage());
        }
    }

    @GetMapping("/getFoodById")
    @CircuitBreaker(name = "foodService", fallbackMethod = "getFoodByIdFallback")
    public ResponseResult<Food> getFoodById(@RequestParam("foodId") Integer foodId) {
        try {
            log.info("Querying food with foodId: {}", foodId);
            Food food = foodService.getFoodById(foodId);
            
            if (food == null) {
                log.warn("No food found for foodId: {}", foodId);
                return ResponseResult.error("没有找到该食品");
            }
            
            // Enhance with frontend-required fields
            food.setDescription(food.getFoodExplain());
            food.setPrice(food.getFoodPrice());
            food.setQuantity(0);
            
            return ResponseResult.success(food);
        } catch (Exception e) {
            log.error("Error querying food with foodId: {}", foodId, e);
            return ResponseResult.error("查询食品详情时出错: " + e.getMessage());
        }
    }

    // Fallback methods
    public ResponseResult<List<Food>> listFoodByBusinessIdFallback(Integer businessId, Throwable t) {
        log.error("Circuit breaker fallback: listFoodByBusinessId failed", t);
        return ResponseResult.error("服务降级：获取食品列表失败");
    }

    public ResponseResult<Food> getFoodByIdFallback(Integer foodId, Throwable t) {
        log.error("Circuit breaker fallback: getFoodById failed", t);
        return ResponseResult.error("服务降级：获取食品详情失败");
    }
} 