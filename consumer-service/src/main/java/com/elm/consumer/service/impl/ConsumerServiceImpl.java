package com.elm.consumer.service.impl;

import com.elm.common.entity.Business;
import com.elm.common.entity.Food;
import com.elm.common.entity.User;
import com.elm.common.result.ResponseResult;
import com.elm.consumer.feign.*;
import com.elm.consumer.service.ConsumerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class ConsumerServiceImpl implements ConsumerService {

    @Autowired
    private UserClient userClient;
    
    @Autowired
    private BusinessClient businessClient;
    
    @Autowired
    private FoodClient foodClient;
    
    @Autowired
    private CartClient cartClient;
    
    @Autowired
    private OrderClient orderClient;
    
    @Autowired
    private AddressClient addressClient;

    @Override
    public ResponseResult<?> login(String userId, String password) {
        log.info("用户登录请求: userId={}", userId);
        return userClient.loginByIdPassword(userId, password);
    }

    @Override
    public ResponseResult<?> listBusinesses(Integer orderTypeId, Integer categoryId, String userId) {
        log.info("获取商家列表: orderTypeId={}, categoryId={}, userId={}", orderTypeId, categoryId, userId);
        return businessClient.listBusinessByOrderTypeId(orderTypeId, categoryId, userId);
    }

    @Override
    public ResponseResult<?> getBusinessWithFoods(Integer businessId, String userId) {
        log.info("获取商家详情及食品列表: businessId={}, userId={}", businessId, userId);
        
        // 1. 获取商家信息
        ResponseResult<Business> businessResult = businessClient.getBusinessById(businessId);
        if (!isSuccess(businessResult)) {
            return businessResult;
        }
        
        // 2. 获取食品列表
        ResponseResult<?> foodResult = foodClient.listFoodByBusinessId(businessId);
        if (!isSuccess(foodResult)) {
            return foodResult;
        }
        
        // 3. 组合结果返回
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("business", businessResult.getData());
        resultMap.put("foodList", foodResult.getData());
        
        return ResponseResult.success(resultMap);
    }

    @Override
    public ResponseResult<?> addToCart(String userId, Integer businessId, Integer foodId) {
        log.info("添加商品到购物车: userId={}, businessId={}, foodId={}", userId, businessId, foodId);
        return cartClient.saveCart(userId, businessId, foodId);
    }

    @Override
    public ResponseResult<?> getCart(String userId, Integer businessId) {
        log.info("获取用户购物车: userId={}, businessId={}", userId, businessId);
        return cartClient.listCart(userId, businessId);
    }

    @Override
    public ResponseResult<?> createOrder(Map<String, Object> orderInfo) {
        log.info("创建订单: {}", orderInfo);
        return orderClient.createOrder(orderInfo);
    }

    @Override
    public ResponseResult<?> getAddresses(String userId) {
        log.info("获取用户地址列表: userId={}", userId);
        return addressClient.listDeliveryAddressByUserId(userId);
    }
    
    // 判断响应是否成功
    private boolean isSuccess(ResponseResult<?> response) {
        return response != null && 
               (response.getCode() == 1 || response.getCode() == 200) && 
               response.getData() != null;
    }
} 