package com.elm.common.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeliveryAddress implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Integer daId;
    private String userId;
    private String contactName;
    private String contactSex;
    private String contactTel;
    private String address;
} 