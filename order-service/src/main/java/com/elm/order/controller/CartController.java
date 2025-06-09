package com.elm.order.controller;

import com.elm.common.entity.Cart;
import com.elm.common.result.ResponseResult;
import com.elm.order.service.CartService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@Slf4j
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/listCart")
    @CircuitBreaker(name = "cartService", fallbackMethod = "listCartFallback")
    public ResponseResult<List<Cart>> listCart(@RequestParam("userId") Integer userId, 
                                              @RequestParam(value = "businessId", required = false) Integer businessId) {
        List<Cart> cartList;
        if (businessId != null) {
            cartList = cartService.listCart(userId, businessId);
        } else {
            cartList = cartService.listCartByUserId(userId);
        }
        
        // Enhance with frontend-required fields
        cartList.forEach(cart -> {
            if (cart.getFood() != null) {
                cart.getFood().setDescription(cart.getFood().getFoodExplain());
                cart.getFood().setPrice(cart.getFood().getFoodPrice());
            }
        });
        
        return ResponseResult.success(cartList);
    }

    @PostMapping("/saveCart")
    @CircuitBreaker(name = "cartService", fallbackMethod = "saveCartFallback")
    public ResponseResult<Integer> saveCart(@RequestParam("userId") Integer userId,
                                          @RequestParam("businessId") Integer businessId,
                                          @RequestParam("foodId") Integer foodId) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setBusinessId(businessId);
        cart.setFoodId(foodId);
        
        int result = cartService.saveCart(cart);
        if (result > 0) {
            return ResponseResult.success(1);
        } else {
            return ResponseResult.error("添加购物车失败");
        }
    }

    @PostMapping("/updateCart")
    @CircuitBreaker(name = "cartService", fallbackMethod = "updateCartFallback")
    public ResponseResult<Integer> updateCart(@RequestParam("userId") Integer userId,
                                           @RequestParam("businessId") Integer businessId,
                                           @RequestParam("foodId") Integer foodId,
                                           @RequestParam("quantity") Integer quantity) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setBusinessId(businessId);
        cart.setFoodId(foodId);
        cart.setQuantity(quantity);
        
        int result = cartService.updateCart(cart);
        if (result > 0) {
            return ResponseResult.success(1);
        } else {
            return ResponseResult.error("更新购物车失败");
        }
    }

    @PostMapping("/removeCart")
    @CircuitBreaker(name = "cartService", fallbackMethod = "removeCartFallback")
    public ResponseResult<Integer> removeCart(@RequestParam("userId") Integer userId,
                                           @RequestParam("businessId") Integer businessId,
                                           @RequestParam("foodId") Integer foodId) {
        int result = cartService.removeCart(userId, businessId, foodId);
        if (result > 0) {
            return ResponseResult.success(1);
        } else {
            return ResponseResult.error("删除购物车失败");
        }
    }

    // Fallback methods
    public ResponseResult<List<Cart>> listCartFallback(Integer userId, Integer businessId, Throwable t) {
        log.error("Circuit breaker fallback: listCart failed", t);
        return ResponseResult.error("服务降级：获取购物车列表失败");
    }

    public ResponseResult<Integer> saveCartFallback(Integer userId, Integer businessId, Integer foodId, Throwable t) {
        log.error("Circuit breaker fallback: saveCart failed", t);
        return ResponseResult.error("服务降级：添加购物车失败");
    }

    public ResponseResult<Integer> updateCartFallback(Integer userId, Integer businessId, Integer foodId, Integer quantity, Throwable t) {
        log.error("Circuit breaker fallback: updateCart failed", t);
        return ResponseResult.error("服务降级：更新购物车失败");
    }

    public ResponseResult<Integer> removeCartFallback(Integer userId, Integer businessId, Integer foodId, Throwable t) {
        log.error("Circuit breaker fallback: removeCart failed", t);
        return ResponseResult.error("服务降级：删除购物车失败");
    }
} 