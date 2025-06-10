package com.elm.common.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Integer orderId;
    private String userId;
    private Integer businessId;
    private String orderDate;
    private Double orderTotal;
    private Integer daId;
    private Integer orderState;
    
    // For display
    private Business business;
    private List<OrderDetail> orderDetailList;
    
    // 前端需要使用list来访问订单明细
    private List<OrderDetail> list;
    
    // 设置订单明细，同时也设置list字段
    public void setOrderDetailList(List<OrderDetail> orderDetailList) {
        this.orderDetailList = orderDetailList;
        this.list = orderDetailList;
    }
} 