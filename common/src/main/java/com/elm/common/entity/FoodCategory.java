package com.elm.common.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class FoodCategory implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Integer categoryId;
    private String name;
    private String imgUrl;
} 