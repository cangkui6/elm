package com.elm.business.controller;

import com.elm.business.service.FoodCategoryService;
import com.elm.common.entity.FoodCategory;
import com.elm.common.result.ResponseResult;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/foodCategory")
@Slf4j
public class FoodCategoryController {

    @Autowired
    private FoodCategoryService foodCategoryService;

    @GetMapping("/listFoodCategories")
    @CircuitBreaker(name = "foodCategoryService", fallbackMethod = "listFoodCategoriesFallback")
    public ResponseResult<List<FoodCategory>> listFoodCategories() {
        List<FoodCategory> categories = foodCategoryService.listFoodCategories();
        return ResponseResult.success(categories);
    }

    // Fallback method
    public ResponseResult<List<FoodCategory>> listFoodCategoriesFallback(Throwable t) {
        log.error("Circuit breaker fallback: listFoodCategories failed", t);
        return ResponseResult.error("服务降级：获取食品分类列表失败");
    }
} 