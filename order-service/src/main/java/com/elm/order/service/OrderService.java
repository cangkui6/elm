package com.elm.order.service;

import com.elm.common.entity.Order;
import com.elm.common.entity.OrderDetail;

import java.util.List;

public interface OrderService {

    /**
     * 创建订单及订单明细
     * @param order 订单信息
     * @param orderDetailList 订单明细列表
     * @return 订单ID
     */
    Integer createOrder(Order order, List<OrderDetail> orderDetailList);
    
    /**
     * 根据订单ID获取订单信息
     * @param orderId 订单ID
     * @return 订单信息
     */
    Order getOrderById(Integer orderId);
    
    /**
     * 根据用户ID获取订单列表
     * @param userId 用户ID
     * @return 订单列表
     */
    List<Order> listOrdersByUserId(String userId);
    
    /**
     * 更新订单状态
     * @param orderId 订单ID
     * @param orderState 订单状态
     * @return 受影响的行数
     */
    Integer updateOrderState(Integer orderId, Integer orderState);
} 