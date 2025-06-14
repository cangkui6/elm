package com.elm.consumer.feign;

import com.elm.common.entity.Order;
import com.elm.common.result.ResponseResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class OrderClientFallback implements OrderClient {

    @Override
    public ResponseResult<Integer> createOrder(Map<String, Object> requestMap) {
        return ResponseResult.error("呃呃...服务器忙中...");
    }

    @Override
    public ResponseResult<Order> getOrderById(Integer orderId) {
        return ResponseResult.error("呃呃...服务器忙中...");
    }

    @Override
    public ResponseResult<List<Order>> listOrdersByUserId(String userId) {
        return ResponseResult.error("呃呃...服务器忙中...");
    }
    
    @Override
    public ResponseResult<List<Order>> listOrdersByUserIdPost(String userId) {
        return ResponseResult.error("呃呃...服务器忙中...");
    }

    @Override
    public ResponseResult<Integer> updateOrderState(Integer orderId, Integer orderState) {
        return ResponseResult.error("呃呃...服务器忙中...");
    }
} 