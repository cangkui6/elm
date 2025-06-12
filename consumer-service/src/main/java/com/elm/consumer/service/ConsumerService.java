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
     * 用户注册
     * @param userId 用户ID
     * @param password 密码
     * @param userName 用户名
     * @param userSex 性别
     * @return 注册结果
     */
    ResponseResult<?> saveUser(String userId, String password, String userName, String userSex);
    
    /**
     * 检查用户是否存在
     * @param userId 用户ID
     * @return 用户是否存在
     */
    ResponseResult<?> getUserById(String userId);
    
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
     * 更新购物车商品数量
     * @param userId 用户ID
     * @param businessId 商家ID
     * @param foodId 食品ID
     * @param quantity 数量
     * @return 更新结果
     */
    ResponseResult<?> updateCart(String userId, Integer businessId, Integer foodId, Integer quantity);
    
    /**
     * 获取用户购物车
     * @param userId 用户ID
     * @param businessId 商家ID
     * @return 购物车信息
     */
    ResponseResult<?> getCart(String userId, Integer businessId);
    
    /**
     * 获取用户在各商家的购物车数量
     * @param userId 用户ID
     * @return 商家ID到购物车数量的映射
     */
    ResponseResult<?> getCartQuantityMap(String userId);
    
    /**
     * 创建订单
     * @param orderInfo 订单信息
     * @return 订单创建结果
     */
    ResponseResult<?> createOrder(Map<String, Object> orderInfo);
    
    /**
     * 根据订单ID获取订单详情
     * @param orderId 订单ID
     * @return 订单详情
     */
    ResponseResult<?> getOrderById(Integer orderId);
    
    /**
     * 更新订单状态
     * @param orderId 订单ID
     * @param orderState 订单状态
     * @return 更新结果
     */
    ResponseResult<?> updateOrderState(Integer orderId, Integer orderState);
    
    /**
     * 获取用户订单列表
     * @param userId 用户ID
     * @return 订单列表
     */
    ResponseResult<?> listOrdersByUserId(String userId);
    
    /**
     * 获取用户地址列表
     * @param userId 用户ID
     * @return 地址列表
     */
    ResponseResult<?> getAddresses(String userId);
    
    /**
     * 更新用户配送地址
     * @param daId 地址ID
     * @param userId 用户ID
     * @param contactName 联系人姓名
     * @param contactSex 联系人性别
     * @param contactTel 联系人电话
     * @param address 地址详情
     * @return 更新结果
     */
    ResponseResult<?> updateDeliveryAddress(Integer daId, String userId, String contactName, 
                                          String contactSex, String contactTel, String address);
    
    /**
     * 新增用户配送地址
     * @param userId 用户ID
     * @param contactName 联系人姓名
     * @param contactSex 联系人性别
     * @param contactTel 联系人电话
     * @param address 地址详情
     * @return 保存结果
     */
    ResponseResult<?> saveDeliveryAddress(String userId, String contactName, 
                                        String contactSex, String contactTel, String address);
    
    /**
     * 删除用户配送地址
     * @param daId 地址ID
     * @return 删除结果
     */
    ResponseResult<?> removeDeliveryAddress(Integer daId);
} 