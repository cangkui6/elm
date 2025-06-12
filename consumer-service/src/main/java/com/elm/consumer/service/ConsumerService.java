package com.elm.consumer.service;

import com.elm.common.result.ResponseResult;

import java.util.Map;

/**
 * 消费者服务接口，用于集成调用各个微服务
 */
public interface ConsumerService {

    /**
     * 用户登录
     * @param userId 用户ID
     * @param password 密码
     * @return 登录结果
     */
    ResponseResult<?> login(String userId, String password);
    
    /**
     * 获取商家列表
     * @param orderTypeId 订单类型ID
     * @param categoryId 分类ID
     * @param userId 用户ID
     * @return 商家列表
     */
    ResponseResult<?> listBusinesses(Integer orderTypeId, Integer categoryId, String userId);
    
    /**
     * 获取商家详情及其食品列表
     * @param businessId 商家ID
     * @param userId 用户ID
     * @return 商家详情和食品列表
     */
    ResponseResult<?> getBusinessWithFoods(Integer businessId, String userId);
    
    /**
     * 添加商品到购物车
     * @param userId 用户ID
     * @param businessId 商家ID
     * @param foodId 食品ID
     * @return 添加结果
     */
    ResponseResult<?> addToCart(String userId, Integer businessId, Integer foodId);
    
    /**
     * 获取用户购物车
     * @param userId 用户ID
     * @param businessId 商家ID
     * @return 购物车信息
     */
    ResponseResult<?> getCart(String userId, Integer businessId);
    
    /**
     * 创建订单
     * @param orderInfo 订单信息
     * @return 订单创建结果
     */
    ResponseResult<?> createOrder(Map<String, Object> orderInfo);
    
    /**
     * 获取用户地址列表
     * @param userId 用户ID
     * @return 地址列表
     */
    ResponseResult<?> getAddresses(String userId);
} 