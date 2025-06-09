package com.elm.common.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Integer userId;
    private String username;
    private String password;
    private String phone;
    private String userSex;
    private String userImg;
    private Integer delTag;
} 