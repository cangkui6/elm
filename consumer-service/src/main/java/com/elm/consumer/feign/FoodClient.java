package com.elm.consumer.feign;

import com.elm.common.entity.Food;
import com.elm.common.result.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "food-service", fallback = FoodClientFallback.class)
public interface FoodClient {

    @PostMapping("/food/listFoodByBusinessId")
    ResponseResult<List<Food>> listFoodByBusinessId(@RequestParam("businessId") Integer businessId);

    @GetMapping("/food/getFoodById")
    ResponseResult<Food> getFoodById(@RequestParam("foodId") Integer foodId);
} 