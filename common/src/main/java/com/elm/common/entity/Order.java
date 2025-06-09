package com.elm.common.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Integer orderId;
    private Integer userId;
    private Integer businessId;
    private String orderDate;
    private Double orderTotal;
    private Integer daId;
    private Integer orderState;
    
    // For display
    private Business business;
    private List<OrderDetail> orderDetailList;
} 