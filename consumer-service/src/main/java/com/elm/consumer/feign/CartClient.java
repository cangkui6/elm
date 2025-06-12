package com.elm.consumer.feign;

import com.elm.common.entity.Cart;
import com.elm.common.result.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "cart-service", fallback = CartClientFallback.class)
public interface CartClient {

    @PostMapping("/cart/listCart")
    ResponseResult<List<Cart>> listCart(@RequestParam("userId") String userId,
                                      @RequestParam(value = "businessId", required = false) Integer businessId);

    @PostMapping("/cart/saveCart")
    ResponseResult<Integer> saveCart(@RequestParam("userId") String userId,
                                   @RequestParam("businessId") Integer businessId,
                                   @RequestParam("foodId") Integer foodId);

    @PostMapping("/cart/updateCart")
    ResponseResult<Integer> updateCart(@RequestParam("userId") String userId,
                                     @RequestParam("businessId") Integer businessId,
                                     @RequestParam("foodId") Integer foodId,
                                     @RequestParam("quantity") Integer quantity);

    @PostMapping("/cart/removeCart")
    ResponseResult<Integer> removeCart(@RequestParam("userId") String userId,
                                     @RequestParam("businessId") Integer businessId,
                                     @RequestParam("foodId") Integer foodId);
    
    @PostMapping("/cart/getCartQuantityMap")
    ResponseResult<?> getCartQuantityMap(@RequestParam("userId") String userId);
} 