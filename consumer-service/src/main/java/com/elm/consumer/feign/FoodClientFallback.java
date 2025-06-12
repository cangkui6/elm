package com.elm.consumer.feign;

import com.elm.common.entity.Food;
import com.elm.common.result.ResponseResult;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Primary
public class FoodClientFallback implements FoodClient {

    @Override
    public ResponseResult<List<Food>> listFoodByBusinessId(Integer businessId) {
        return ResponseResult.error("食品服务不可用：获取食品列表失败");
    }

    @Override
    public ResponseResult<Food> getFoodById(Integer foodId) {
        return ResponseResult.error("食品服务不可用：获取食品详情失败");
    }
} 