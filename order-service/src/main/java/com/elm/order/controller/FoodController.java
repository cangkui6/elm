package com.elm.order.controller;

import com.elm.common.entity.Food;
import com.elm.common.result.ResponseResult;
import com.elm.order.mapper.FoodMapper;
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
    private FoodMapper foodMapper;

    /**
     * 根据商家ID获取食品列表
     * @param businessId 商家ID
     * @return 食品列表
     */
    @PostMapping("/listFoodByBusinessId")
    @CircuitBreaker(name = "foodService", fallbackMethod = "listFoodByBusinessIdFallback")
    public ResponseResult<List<Food>> listFoodByBusinessId(@RequestParam("businessId") Integer businessId) {
        List<Food> foodList = foodMapper.listFoodByBusinessId(businessId);
        return ResponseResult.success(foodList);
    }

    /**
     * 根据食品ID获取食品信息
     * @param foodId 食品ID
     * @return 食品信息
     */
    @GetMapping("/getFoodById")
    @CircuitBreaker(name = "foodService", fallbackMethod = "getFoodByIdFallback")
    public ResponseResult<Food> getFoodById(@RequestParam("foodId") Integer foodId) {
        Food food = foodMapper.getFoodById(foodId);
        if (food != null) {
            return ResponseResult.success(food);
        } else {
            return ResponseResult.error("食品不存在");
        }
    }

    // Fallback methods
    public ResponseResult<List<Food>> listFoodByBusinessIdFallback(Integer businessId, Throwable t) {
        log.error("Circuit breaker fallback: listFoodByBusinessId failed", t);
        return ResponseResult.error("服务降级：获取食品列表失败");
    }

    public ResponseResult<Food> getFoodByIdFallback(Integer foodId, Throwable t) {
        log.error("Circuit breaker fallback: getFoodById failed", t);
        return ResponseResult.error("服务降级：获取食品信息失败");
    }
} 