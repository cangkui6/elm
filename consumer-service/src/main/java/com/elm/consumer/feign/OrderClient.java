package com.elm.consumer.feign;

import com.elm.common.entity.Order;
import com.elm.common.result.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "order-service", fallback = OrderClientFallback.class)
public interface OrderClient {

    @PostMapping("/order/createOrder")
    ResponseResult<Integer> createOrder(@RequestBody Map<String, Object> requestMap);

    @GetMapping("/order/getOrderById")
    ResponseResult<Order> getOrderById(@RequestParam("orderId") Integer orderId);

    @GetMapping("/order/listOrdersByUserId")
    ResponseResult<List<Order>> listOrdersByUserId(@RequestParam("userId") String userId);
    
    @PostMapping("/order/listOrdersByUserId")
    ResponseResult<List<Order>> listOrdersByUserIdPost(@RequestParam("userId") String userId);

    @PutMapping("/order/updateOrderState")
    ResponseResult<Integer> updateOrderState(@RequestParam("orderId") Integer orderId, 
                                           @RequestParam("orderState") Integer orderState);
} 