package com.elm.common.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Cart implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Integer cartId;
    private Integer foodId;
    private Integer businessId;
    private Integer userId;
    private Integer quantity;
    
    // For frontend display
    private Business business;
    private Food food;
} 