package com.elm.consumer.service.impl;

import com.elm.common.entity.Business;
import com.elm.common.entity.Food;
import com.elm.common.entity.User;
import com.elm.common.entity.Order;
import com.elm.common.result.ResponseResult;
import com.elm.consumer.feign.*;
import com.elm.consumer.service.ConsumerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

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
    public ResponseResult<?> saveUser(String userId, String password, String userName, String userSex) {
        log.info("用户注册请求: userId={}, userName={}", userId, userName);
        return userClient.saveUser(userId, password, userName, userSex);
    }
    
    @Override
    public ResponseResult<?> getUserById(String userId) {
        log.info("检查用户是否存在: userId={}", userId);
        return userClient.checkUserExists(userId);
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
    public ResponseResult<?> updateCart(String userId, Integer businessId, Integer foodId, Integer quantity) {
        log.info("更新购物车商品数量: userId={}, businessId={}, foodId={}, quantity={}", userId, businessId, foodId, quantity);
        return cartClient.updateCart(userId, businessId, foodId, quantity);
    }

    @Override
    public ResponseResult<?> getCart(String userId, Integer businessId) {
        log.info("获取用户购物车: userId={}, businessId={}", userId, businessId);
        return cartClient.listCart(userId, businessId);
    }
    
    @Override
    public ResponseResult<?> getCartQuantityMap(String userId) {
        log.info("获取用户在各商家的购物车数量: userId={}", userId);
        return cartClient.getCartQuantityMap(userId);
    }

    @Override
    public ResponseResult<?> createOrder(Map<String, Object> orderInfo) {
        log.info("创建订单: {}", orderInfo);
        return orderClient.createOrder(orderInfo);
    }

    @Override
    public ResponseResult<?> getOrderById(Integer orderId) {
        log.info("获取订单详情: orderId={}", orderId);
        return orderClient.getOrderById(orderId);
    }

    @Override
    public ResponseResult<?> updateOrderState(Integer orderId, Integer orderState) {
        log.info("更新订单状态: orderId={}, orderState={}", orderId, orderState);
        return orderClient.updateOrderState(orderId, orderState);
    }
    
    @Override
    public ResponseResult<?> listOrdersByUserId(String userId) {
        log.info("获取用户订单列表: userId={}", userId);
        return orderClient.listOrdersByUserId(userId);
    }

    @Override
    public ResponseResult<?> getAddresses(String userId) {
        log.info("获取用户地址列表: userId={}", userId);
        return addressClient.listDeliveryAddressByUserId(userId);
    }
    
    @Override
    public ResponseResult<?> updateDeliveryAddress(Integer daId, String userId, String contactName, 
                                                 String contactSex, String contactTel, String address) {
        log.info("更新用户配送地址: daId={}, userId={}, contactName={}", daId, userId, contactName);
        return addressClient.updateDeliveryAddress(daId, userId, contactName, contactSex, contactTel, address);
    }
    
    @Override
    public ResponseResult<?> saveDeliveryAddress(String userId, String contactName, 
                                               String contactSex, String contactTel, String address) {
        log.info("新增用户配送地址: userId={}, contactName={}", userId, contactName);
        return addressClient.saveDeliveryAddress(userId, contactName, contactSex, contactTel, address);
    }
    
    @Override
    public ResponseResult<?> removeDeliveryAddress(Integer daId) {
        log.info("删除用户配送地址: daId={}", daId);
        return addressClient.removeDeliveryAddress(daId);
    }
    
    // 判断响应是否成功
    private boolean isSuccess(ResponseResult<?> response) {
        return response != null && 
               (response.getCode() == 1 || response.getCode() == 200) && 
               response.getData() != null;
    }
}