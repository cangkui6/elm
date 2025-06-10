package com.elm.business.client;

import com.elm.common.entity.Cart;
import com.elm.common.result.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "order-service", fallbackFactory = OrderServiceFallbackFactory.class, configuration = FeignConfig.class)
public interface OrderServiceClient {

    /**
     * 查询购物车列表
     */
    @PostMapping("/cart/listCart")
    ResponseResult<List<Cart>> listCart(@RequestParam("userId") String userId, 
                                       @RequestParam(value = "businessId", required = false) Integer businessId);
                                       
    /**
     * 添加商品到购物车
     */
    @PostMapping("/cart/saveCart")
    ResponseResult<Integer> saveCart(@RequestParam("userId") String userId,
                                    @RequestParam("businessId") Integer businessId,
                                    @RequestParam("foodId") Integer foodId);
                                    
    /**
     * 更新购物车数量
     */
    @PostMapping("/cart/updateCart")
    ResponseResult<Integer> updateCart(@RequestParam("userId") String userId,
                                     @RequestParam("businessId") Integer businessId,
                                     @RequestParam("foodId") Integer foodId,
                                     @RequestParam("quantity") Integer quantity);
                                     
    /**
     * 删除购物车商品
     */
    @PostMapping("/cart/removeCart")
    ResponseResult<Integer> removeCart(@RequestParam("userId") String userId,
                                     @RequestParam("businessId") Integer businessId,
                                     @RequestParam("foodId") Integer foodId);
} 