package com.elm.consumer.controller;

import com.elm.common.result.ResponseResult;
import com.elm.consumer.service.ConsumerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/consumer")
public class ConsumerController {

    @Autowired
    private ConsumerService consumerService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseResult<?> login(@RequestParam("userId") String userId, 
                                  @RequestParam("password") String password) {
        return consumerService.login(userId, password);
    }

    /**
     * 用户注册
     */
    @PostMapping("/user/saveUser")
    public ResponseResult<?> saveUser(@RequestParam("userId") String userId,
                                     @RequestParam("password") String password,
                                     @RequestParam("userName") String userName,
                                     @RequestParam("userSex") String userSex) {
        return consumerService.saveUser(userId, password, userName, userSex);
    }

    /**
     * 检查用户是否存在
     */
    @PostMapping("/user/getUserById")
    public ResponseResult<?> getUserById(@RequestParam("userId") String userId) {
        return consumerService.getUserById(userId);
    }

    /**
     * 获取商家列表
     */
    @GetMapping("/businesses")
    public ResponseResult<?> listBusinesses(
            @RequestParam(value = "orderTypeId", required = false) Integer orderTypeId,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "userId", required = false) String userId) {
        return consumerService.listBusinesses(orderTypeId, categoryId, userId);
    }

    /**
     * 获取商家详情及其食品列表
     */
    @GetMapping("/business/{businessId}")
    public ResponseResult<?> getBusinessWithFoods(
            @PathVariable("businessId") Integer businessId,
            @RequestParam(value = "userId", required = false) String userId) {
        return consumerService.getBusinessWithFoods(businessId, userId);
    }

    /**
     * 添加商品到购物车
     */
    @PostMapping("/cart/add")
    public ResponseResult<?> addToCart(
            @RequestParam("userId") String userId,
            @RequestParam("businessId") Integer businessId,
            @RequestParam("foodId") Integer foodId) {
        return consumerService.addToCart(userId, businessId, foodId);
    }

    /**
     * 更新购物车商品数量
     */
    @PostMapping("/cart/update")
    public ResponseResult<?> updateCart(
            @RequestParam("userId") String userId,
            @RequestParam("businessId") Integer businessId,
            @RequestParam("foodId") Integer foodId,
            @RequestParam("quantity") Integer quantity) {
        return consumerService.updateCart(userId, businessId, foodId, quantity);
    }

    /**
     * 获取用户购物车
     */
    @GetMapping("/cart")
    public ResponseResult<?> getCart(
            @RequestParam("userId") String userId,
            @RequestParam(value = "businessId", required = false) Integer businessId) {
        return consumerService.getCart(userId, businessId);
    }

    /**
     * 获取用户在各商家的购物车数量
     */
    @PostMapping("/cart/getCartQuantityMap")
    public ResponseResult<?> getCartQuantityMap(@RequestParam("userId") String userId) {
        return consumerService.getCartQuantityMap(userId);
    }

    /**
     * 创建订单
     */
    @PostMapping("/order/create")
    public ResponseResult<?> createOrder(@RequestBody Map<String, Object> orderInfo) {
        return consumerService.createOrder(orderInfo);
    }

    /**
     * 根据订单ID获取订单详情
     */
    @GetMapping("/order/getOrderById")
    public ResponseResult<?> getOrderById(@RequestParam("orderId") Integer orderId) {
        return consumerService.getOrderById(orderId);
    }

    /**
     * 更新订单状态
     */
    @PutMapping("/order/updateOrderState")
    public ResponseResult<?> updateOrderState(
            @RequestParam("orderId") Integer orderId,
            @RequestParam("orderState") Integer orderState) {
        return consumerService.updateOrderState(orderId, orderState);
    }

    /**
     * 获取用户订单列表
     */
    @GetMapping("/order/listOrdersByUserId")
    public ResponseResult<?> listOrdersByUserId(@RequestParam("userId") String userId) {
        return consumerService.listOrdersByUserId(userId);
    }

    /**
     * 获取用户地址列表
     */
    @GetMapping("/addresses")
    public ResponseResult<?> getAddresses(@RequestParam("userId") String userId) {
        return consumerService.getAddresses(userId);
    }

    /**
     * 更新用户配送地址
     */
    @PutMapping("/deliveryAddress/updateDeliveryAddress")
    public ResponseResult<?> updateDeliveryAddress(
            @RequestParam("daId") Integer daId,
            @RequestParam("userId") String userId,
            @RequestParam("contactName") String contactName,
            @RequestParam("contactSex") String contactSex,
            @RequestParam("contactTel") String contactTel,
            @RequestParam("address") String address) {
        return consumerService.updateDeliveryAddress(daId, userId, contactName, contactSex, contactTel, address);
    }

    /**
     * 新增用户配送地址
     */
    @PostMapping("/deliveryAddress/saveDeliveryAddress")
    public ResponseResult<?> saveDeliveryAddress(
            @RequestParam("userId") String userId,
            @RequestParam("contactName") String contactName,
            @RequestParam("contactSex") String contactSex,
            @RequestParam("contactTel") String contactTel,
            @RequestParam("address") String address) {
        return consumerService.saveDeliveryAddress(userId, contactName, contactSex, contactTel, address);
    }

    /**
     * 删除用户配送地址
     */
    @DeleteMapping("/deliveryAddress/removeDeliveryAddress")
    public ResponseResult<?> removeDeliveryAddress(@RequestParam("daId") Integer daId) {
        return consumerService.removeDeliveryAddress(daId);
    }
} 