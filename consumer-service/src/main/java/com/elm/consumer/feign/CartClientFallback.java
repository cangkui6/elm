package com.elm.consumer.feign;

import com.elm.common.entity.Cart;
import com.elm.common.result.ResponseResult;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CartClientFallback implements CartClient {

    @Override
    public ResponseResult<List<Cart>> listCart(String userId, Integer businessId) {
        return ResponseResult.error("呃呃...服务器忙中...");
    }

    @Override
    public ResponseResult<Integer> saveCart(String userId, Integer businessId, Integer foodId) {
        return ResponseResult.error("呃呃...服务器忙中...");
    }

    @Override
    public ResponseResult<Integer> updateCart(String userId, Integer businessId, Integer foodId, Integer quantity) {
        return ResponseResult.error("呃呃...服务器忙中...");
    }

    @Override
    public ResponseResult<Integer> removeCart(String userId, Integer businessId, Integer foodId) {
        return ResponseResult.error("呃呃...服务器忙中...");
    }
    
    @Override
    public ResponseResult<?> getCartQuantityMap(String userId) {
        return ResponseResult.error("呃呃...服务器忙中...");
    }
} 