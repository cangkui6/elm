package com.elm.common.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderDetail implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Integer odId;
    private Integer orderId;
    private Integer foodId;
    private Integer quantity;
    
    // For display
    private Food food;
} 