package com.elm.order.controller;

import com.elm.common.entity.Order;
import com.elm.common.entity.OrderDetail;
import com.elm.common.result.ResponseResult;
import com.elm.order.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 创建订单
     * @param requestMap 包含订单信息和订单明细列表的Map
     * @return 订单ID
     */
    @PostMapping("/createOrder")
    @CircuitBreaker(name = "orderService", fallbackMethod = "createOrderFallback")
    public ResponseResult<Integer> createOrder(@RequestBody Map<String, Object> requestMap) {
        try {
            // 从请求中获取订单信息
            Order order = new Order();
            order.setUserId((String) requestMap.get("userId"));
            order.setBusinessId((Integer) requestMap.get("businessId"));
            order.setDaId((Integer) requestMap.get("daId"));
            order.setOrderTotal(Double.parseDouble(requestMap.get("orderTotal").toString()));
            
            // 设置订单日期
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            order.setOrderDate(sdf.format(new Date()));
            
            // 设置订单状态为未支付
            order.setOrderState(0);
            
            // 从请求中获取订单明细列表
            List<Map<String, Object>> detailMapList = (List<Map<String, Object>>) requestMap.get("orderDetailList");
            List<OrderDetail> orderDetailList = new ArrayList<>();
            
            for (Map<String, Object> detailMap : detailMapList) {
                OrderDetail detail = new OrderDetail();
                detail.setFoodId((Integer) detailMap.get("foodId"));
                detail.setQuantity((Integer) detailMap.get("quantity"));
                orderDetailList.add(detail);
            }
            
            Integer orderId = orderService.createOrder(order, orderDetailList);
            if (orderId != null) {
                return ResponseResult.success(orderId);
            } else {
                return ResponseResult.error("创建订单失败");
            }
        } catch (Exception e) {
            log.error("创建订单异常", e);
            return ResponseResult.error("创建订单异常：" + e.getMessage());
        }
    }

    /**
     * 根据订单ID获取订单信息
     * @param orderId 订单ID
     * @return 订单信息
     */
    @GetMapping("/getOrderById")
    @CircuitBreaker(name = "orderService", fallbackMethod = "getOrderByIdFallback")
    public ResponseResult<Order> getOrderById(@RequestParam("orderId") Integer orderId) {
        Order order = orderService.getOrderById(orderId);
        if (order != null) {
            return ResponseResult.success(order);
        } else {
            return ResponseResult.error("订单不存在");
        }
    }

    /**
     * 根据用户ID获取订单列表
     * @param userId 用户ID
     * @return 订单列表
     */
    @GetMapping("/listOrdersByUserId")
    @CircuitBreaker(name = "orderService", fallbackMethod = "listOrdersByUserIdFallback")
    public ResponseResult<List<Order>> listOrdersByUserId(@RequestParam("userId") String userId) {
        List<Order> orderList = orderService.listOrdersByUserId(userId);
        return ResponseResult.success(orderList);
    }

    /**
     * 更新订单状态
     * @param orderId 订单ID
     * @param orderState 订单状态
     * @return 更新结果
     */
    @PutMapping("/updateOrderState")
    @CircuitBreaker(name = "orderService", fallbackMethod = "updateOrderStateFallback")
    public ResponseResult<Integer> updateOrderState(@RequestParam("orderId") Integer orderId, @RequestParam("orderState") Integer orderState) {
        Integer result = orderService.updateOrderState(orderId, orderState);
        if (result > 0) {
            return ResponseResult.success(result);
        } else {
            return ResponseResult.error("更新订单状态失败");
        }
    }

    // Fallback methods
    public ResponseResult<Integer> createOrderFallback(Map<String, Object> requestMap, Throwable t) {
        log.error("Circuit breaker fallback: createOrder failed", t);
        return ResponseResult.error("服务降级：创建订单失败");
    }

    public ResponseResult<Order> getOrderByIdFallback(Integer orderId, Throwable t) {
        log.error("Circuit breaker fallback: getOrderById failed", t);
        return ResponseResult.error("服务降级：获取订单失败");
    }

    public ResponseResult<List<Order>> listOrdersByUserIdFallback(String userId, Throwable t) {
        log.error("Circuit breaker fallback: listOrdersByUserId failed", t);
        return ResponseResult.error("服务降级：获取用户订单列表失败");
    }

    public ResponseResult<Integer> updateOrderStateFallback(Integer orderId, Integer orderState, Throwable t) {
        log.error("Circuit breaker fallback: updateOrderState failed", t);
        return ResponseResult.error("服务降级：更新订单状态失败");
    }
} 