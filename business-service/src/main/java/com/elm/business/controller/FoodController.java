package com.elm.business.controller;

import com.elm.business.service.FoodService;
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
        List<Food> foodList = foodService.listFoodByBusinessId(businessId);
        
        // Enhance with frontend-required fields
        foodList.forEach(food -> {
            food.setDescription(food.getFoodExplain());
            food.setPrice(food.getFoodPrice());
            food.setQuantity(0);
        });
        
        return ResponseResult.success(foodList);
    }

    @GetMapping("/getFoodById")
    @CircuitBreaker(name = "foodService", fallbackMethod = "getFoodByIdFallback")
    public ResponseResult<Food> getFoodById(@RequestParam("foodId") Integer foodId) {
        Food food = foodService.getFoodById(foodId);
        
        // Enhance with frontend-required fields
        if (food != null) {
            food.setDescription(food.getFoodExplain());
            food.setPrice(food.getFoodPrice());
            food.setQuantity(0);
        }
        
        return ResponseResult.success(food);
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