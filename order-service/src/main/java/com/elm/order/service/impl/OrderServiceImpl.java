package com.elm.order.service.impl;

import com.elm.common.entity.Order;
import com.elm.common.entity.OrderDetail;
import com.elm.order.mapper.OrderMapper;
import com.elm.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

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
        Order order = orderMapper.getOrderById(orderId);
        if (order != null) {
            // 查询订单明细
            List<OrderDetail> orderDetailList = orderMapper.listOrderDetailsByOrderId(orderId);
            order.setOrderDetailList(orderDetailList);
        }
        return order;
    }

    @Override
    public List<Order> listOrdersByUserId(String userId) {
        List<Order> orderList = orderMapper.listOrdersByUserId(userId);
        // 查询每个订单的明细
        if (orderList != null && !orderList.isEmpty()) {
            for (Order order : orderList) {
                List<OrderDetail> orderDetailList = orderMapper.listOrderDetailsByOrderId(order.getOrderId());
                order.setOrderDetailList(orderDetailList);
            }
        }
        return orderList;
    }

    @Override
    @Transactional
    public Integer updateOrderState(Integer orderId, Integer orderState) {
        return orderMapper.updateOrderState(orderId, orderState);
    }
} 