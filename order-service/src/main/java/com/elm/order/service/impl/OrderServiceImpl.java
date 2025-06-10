package com.elm.order.service.impl;

import com.elm.common.entity.Order;
import com.elm.common.entity.OrderDetail;
import com.elm.common.entity.Business;
import com.elm.common.entity.Food;
import com.elm.order.mapper.OrderMapper;
import com.elm.order.mapper.BusinessMapper;
import com.elm.order.mapper.FoodMapper;
import com.elm.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private BusinessMapper businessMapper;
    
    @Autowired
    private FoodMapper foodMapper;

    @Override
    @Transactional
    public Integer createOrder(Order order, List<OrderDetail> orderDetailList) {
        // 1. 保存订单主表信息
        int orderResult = orderMapper.saveOrder(order);
        
        if (orderResult > 0) {
            // 2. 获取生成的订单ID
            Integer orderId = order.getOrderId();
            
            // 3. 保存订单明细
            for (OrderDetail detail : orderDetailList) {
                detail.setOrderId(orderId);
                orderMapper.saveOrderDetail(detail);
            }
            
            // 4. 清空用户购物车
            orderMapper.removeCart(order.getUserId(), order.getBusinessId());
            
            return orderId;
        }
        
        return null;
    }

    @Override
    public Order getOrderById(Integer orderId) {
        try {
            log.info("查询订单信息，orderId: {}", orderId);
            
            // 1. 使用连接查询获取订单和商家信息
            Order order = orderMapper.getOrderWithBusinessById(orderId);
            
            if (order != null) {
                log.info("订单信息: {}", order);
                
                // 2. 使用连接查询获取订单明细和食品信息
                List<OrderDetail> orderDetailList = orderMapper.listOrderDetailsByOrderId(orderId);
                log.info("查询到 {} 条订单明细", orderDetailList != null ? orderDetailList.size() : 0);
                
                // 确保每个明细的食品信息都正确设置
                if (orderDetailList != null && !orderDetailList.isEmpty()) {
                    for (OrderDetail detail : orderDetailList) {
                        if (detail.getFood() == null) {
                            // 如果映射没有成功自动关联Food，手动查询
                            Food food = foodMapper.getFoodById(detail.getFoodId());
                            detail.setFood(food);
                        }
                    }
                }
                
                order.setOrderDetailList(orderDetailList);
            } else {
                log.warn("未找到订单信息，orderId: {}", orderId);
            }
            
            return order;
        } catch (Exception e) {
            log.error("查询订单信息异常，orderId: {}", orderId, e);
            return null;
        }
    }

    @Override
    public List<Order> listOrdersByUserId(String userId) {
        try {
            log.info("查询用户订单列表，userId: {}", userId);
            
            List<Order> orderList = orderMapper.listOrdersByUserId(userId);
            log.info("查询到 {} 条订单", orderList != null ? orderList.size() : 0);
            
            // 查询每个订单的明细和相关信息
            if (orderList != null && !orderList.isEmpty()) {
                for (Order order : orderList) {
                    try {
                        // 查询订单明细及食品信息
                        List<OrderDetail> orderDetailList = orderMapper.listOrderDetailsByOrderId(order.getOrderId());
                        if (orderDetailList != null && !orderDetailList.isEmpty()) {
                            log.debug("订单 {} 有 {} 个明细项", order.getOrderId(), orderDetailList.size());
                            // 确保每个明细都有food信息
                            for (OrderDetail detail : orderDetailList) {
                                if (detail.getFood() == null) {
                                    Food food = foodMapper.getFoodById(detail.getFoodId());
                                    if (food != null) {
                                        detail.setFood(food);
                                    }
                                }
                            }
                            order.setOrderDetailList(orderDetailList);
                        }
                    } catch (Exception e) {
                        log.error("查询订单 {} 的明细信息异常", order.getOrderId(), e);
                    }
                }
            }
            
            return orderList;
        } catch (Exception e) {
            log.error("查询用户订单列表异常，userId: {}", userId, e);
            return null;
        }
    }

    @Override
    @Transactional
    public Integer updateOrderState(Integer orderId, Integer orderState) {
        try {
            log.info("更新订单状态，orderId: {}, orderState: {}", orderId, orderState);
            int result = orderMapper.updateOrderState(orderId, orderState);
            log.info("更新订单状态结果: {}", result);
            return result;
        } catch (Exception e) {
            log.error("更新订单状态异常，orderId: {}, orderState: {}", orderId, orderState, e);
            return 0;
        }
    }
} 