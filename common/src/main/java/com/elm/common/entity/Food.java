package com.elm.common.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Food implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Integer foodId;
    private String foodName;
    private String foodExplain;
    private String foodImg;
    private Double foodPrice;
    private Integer businessId;
    
    // Fields matching the frontend
    private String description;
    private Double price;
    private Integer quantity; // Used for cart functionality
} 