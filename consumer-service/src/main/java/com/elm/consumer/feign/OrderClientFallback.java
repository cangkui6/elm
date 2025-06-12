package com.elm.consumer.feign;

import com.elm.common.entity.Order;
import com.elm.common.result.ResponseResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

public class OrderClientFallback implements OrderClient {

    @Override
    public ResponseResult<Integer> createOrder(Map<String, Object> requestMap) {
        return ResponseResult.error("訂單服務不可用：創建訂單失敗");
    }

    @Override
    public ResponseResult<Order> getOrderById(Integer orderId) {
        return ResponseResult.error("訂單服務不可用：獲取訂單詳情失敗");
    }

    @Override
    public ResponseResult<List<Order>> listOrdersByUserId(String userId) {
        return ResponseResult.error("訂單服務不可用：獲取訂單列表失敗");
    }
    
    @Override
    public ResponseResult<List<Order>> listOrdersByUserIdPost(String userId) {
        return ResponseResult.error("訂單服務不可用：獲取訂單列表失敗");
    }

    @Override
    public ResponseResult<Integer> updateOrderState(Integer orderId, Integer orderState) {
        return ResponseResult.error("訂單服務不可用：更新訂單狀態失敗");
    }
} 