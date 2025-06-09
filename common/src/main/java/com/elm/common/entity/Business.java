package com.elm.common.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Business implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Integer businessId;
    private String businessName;
    private String businessAddress;
    private String businessExplain;
    private String businessImg;
    private Integer orderTypeId;
    private Double starPrice;
    private Double deliveryPrice;
    private String remarks;
    
    // Fields matching the frontend
    private Double rating;
    private Integer orderCount;
    private Integer monthSales;
    private Double minPrice;
    private String deliveryMethod;
    private String distance;
    private String deliveryTime;
    private String foodType;
    private Integer badge;
} 