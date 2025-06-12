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
     * 获取用户购物车
     */
    @GetMapping("/cart")
    public ResponseResult<?> getCart(
            @RequestParam("userId") String userId,
            @RequestParam(value = "businessId", required = false) Integer businessId) {
        return consumerService.getCart(userId, businessId);
    }

    /**
     * 创建订单
     */
    @PostMapping("/order/create")
    public ResponseResult<?> createOrder(@RequestBody Map<String, Object> orderInfo) {
        return consumerService.createOrder(orderInfo);
    }

    /**
     * 获取用户地址列表
     */
    @GetMapping("/addresses")
    public ResponseResult<?> getAddresses(@RequestParam("userId") String userId) {
        return consumerService.getAddresses(userId);
    }
} 