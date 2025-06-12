package com.elm.consumer.feign;

import com.elm.common.entity.Cart;
import com.elm.common.result.ResponseResult;

import java.util.List;

public class CartClientFallback implements CartClient {

    @Override
    public ResponseResult<List<Cart>> listCart(String userId, Integer businessId) {
        return ResponseResult.error("購物車服務不可用：獲取購物車列表失敗");
    }

    @Override
    public ResponseResult<Integer> saveCart(String userId, Integer businessId, Integer foodId) {
        return ResponseResult.error("購物車服務不可用：添加購物車失敗");
    }

    @Override
    public ResponseResult<Integer> updateCart(String userId, Integer businessId, Integer foodId, Integer quantity) {
        return ResponseResult.error("購物車服務不可用：更新購物車失敗");
    }

    @Override
    public ResponseResult<Integer> removeCart(String userId, Integer businessId, Integer foodId) {
        return ResponseResult.error("購物車服務不可用：刪除購物車失敗");
    }
    
    @Override
    public ResponseResult<?> getCartQuantityMap(String userId) {
        return ResponseResult.error("購物車服務不可用：獲取購物車數量映射失敗");
    }
} 