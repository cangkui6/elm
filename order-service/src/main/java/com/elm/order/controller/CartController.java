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
    public ResponseResult<List<Cart>> listCart(@RequestParam("userId") String userId, 
                                              @RequestParam(value = "businessId", required = false) Integer businessId) {
        try {
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
        } catch (Exception e) {
            log.error("Error listing cart for userId: {}", userId, e);
            return ResponseResult.error("查询购物车失败: " + e.getMessage());
        }
    }

    @PostMapping("/saveCart")
    @CircuitBreaker(name = "cartService", fallbackMethod = "saveCartFallback")
    public ResponseResult<Integer> saveCart(@RequestParam("userId") String userId,
                                          @RequestParam("businessId") Integer businessId,
                                          @RequestParam("foodId") Integer foodId) {
        try {
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setBusinessId(businessId);
        cart.setFoodId(foodId);
            cart.setQuantity(1); // Default to 1 when adding a new item
        
        int result = cartService.saveCart(cart);
        if (result > 0) {
            return ResponseResult.success(1);
        } else {
            return ResponseResult.error("添加购物车失败");
            }
        } catch (Exception e) {
            log.error("Error saving cart for userId: {}", userId, e);
            return ResponseResult.error("添加购物车失败: " + e.getMessage());
        }
    }

    @PostMapping("/updateCart")
    @CircuitBreaker(name = "cartService", fallbackMethod = "updateCartFallback")
    public ResponseResult<Integer> updateCart(@RequestParam("userId") String userId,
                                           @RequestParam("businessId") Integer businessId,
                                           @RequestParam("foodId") Integer foodId,
                                           @RequestParam("quantity") Integer quantity) {
        try {
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
        } catch (Exception e) {
            log.error("Error updating cart for userId: {}", userId, e);
            return ResponseResult.error("更新购物车失败: " + e.getMessage());
        }
    }

    @PostMapping("/removeCart")
    @CircuitBreaker(name = "cartService", fallbackMethod = "removeCartFallback")
    public ResponseResult<Integer> removeCart(@RequestParam("userId") String userId,
                                           @RequestParam("businessId") Integer businessId,
                                           @RequestParam("foodId") Integer foodId) {
        try {
        int result = cartService.removeCart(userId, businessId, foodId);
        if (result > 0) {
            return ResponseResult.success(1);
        } else {
            return ResponseResult.error("删除购物车失败");
        }
        } catch (Exception e) {
            log.error("Error removing from cart for userId: {}", userId, e);
            return ResponseResult.error("删除购物车失败: " + e.getMessage());
        }
    }

    /**
     * 根据用户ID和商家ID查询购物车信息
     * @param userId 用户ID
     * @param businessId 商家ID
     * @return 购物车信息列表
     */
    @PostMapping("/listCartByBusinessId")
    @CircuitBreaker(name = "cartService", fallbackMethod = "listCartByBusinessIdFallback")
    public ResponseResult<List<Cart>> listCartByBusinessId(@RequestParam("userId") String userId, @RequestParam("businessId") Integer businessId) {
        List<Cart> cartList = cartService.listCart(userId, businessId);
        return ResponseResult.success(cartList);
    }

    // Fallback methods
    public ResponseResult<List<Cart>> listCartFallback(String userId, Integer businessId, Throwable t) {
        log.error("Circuit breaker fallback: listCart failed", t);
        return ResponseResult.error("服务降级：获取购物车列表失败");
    }

    public ResponseResult<Integer> saveCartFallback(String userId, Integer businessId, Integer foodId, Throwable t) {
        log.error("Circuit breaker fallback: saveCart failed", t);
        return ResponseResult.error("服务降级：添加购物车失败");
    }

    public ResponseResult<Integer> updateCartFallback(String userId, Integer businessId, Integer foodId, Integer quantity, Throwable t) {
        log.error("Circuit breaker fallback: updateCart failed", t);
        return ResponseResult.error("服务降级：更新购物车失败");
    }

    public ResponseResult<Integer> removeCartFallback(String userId, Integer businessId, Integer foodId, Throwable t) {
        log.error("Circuit breaker fallback: removeCart failed", t);
        return ResponseResult.error("服务降级：删除购物车失败");
    }

    public ResponseResult<List<Cart>> listCartByBusinessIdFallback(String userId, Integer businessId, Throwable t) {
        log.error("Circuit breaker fallback: listCartByBusinessId failed", t);
        return ResponseResult.error("服务降级：获取购物车信息失败");
    }
} 