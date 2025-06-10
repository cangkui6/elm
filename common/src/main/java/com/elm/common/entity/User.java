package com.elm.common.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String userId;    // 用户编号 - primary key
    private String password;  // 密码
    private String userName;  // 用户名称
    private Integer userSex;  // 用户性别（1：男； 0：女）
    private String userImg;   // 用户头像
    private Integer delTag;   // 删除标记（1：正常； 0：删除）
} 