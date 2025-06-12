package com.elm.consumer.feign;

import com.elm.common.entity.Cart;
import com.elm.common.result.ResponseResult;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Primary
public class CartClientFallback implements CartClient {

    @Override
    public ResponseResult<List<Cart>> listCart(String userId, Integer businessId) {
        return ResponseResult.error("购物车服务不可用：获取购物车列表失败");
    }

    @Override
    public ResponseResult<Integer> saveCart(String userId, Integer businessId, Integer foodId) {
        return ResponseResult.error("购物车服务不可用：添加商品到购物车失败");
    }

    @Override
    public ResponseResult<Integer> updateCart(String userId, Integer businessId, Integer foodId, Integer quantity) {
        return ResponseResult.error("购物车服务不可用：更新购物车失败");
    }

    @Override
    public ResponseResult<Integer> removeCart(String userId, Integer businessId, Integer foodId) {
        return ResponseResult.error("购物车服务不可用：删除购物车商品失败");
    }
    
    @Override
    public ResponseResult<?> getCartQuantityMap(String userId) {
        return ResponseResult.error("购物车服务不可用：获取购物车数量统计失败");
    }
} 