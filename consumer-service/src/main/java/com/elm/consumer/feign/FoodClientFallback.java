package com.elm.consumer.feign;

import com.elm.common.entity.Food;
import com.elm.common.result.ResponseResult;

import java.util.List;

public class FoodClientFallback implements FoodClient {

    @Override
    public ResponseResult<Food> getFoodById(Integer foodId) {
        return ResponseResult.error("食品服務不可用：獲取食品信息失敗");
    }

    @Override
    public ResponseResult<List<Food>> listFoodByBusinessId(Integer businessId) {
        return ResponseResult.error("食品服務不可用：獲取食品列表失敗");
    }
} 