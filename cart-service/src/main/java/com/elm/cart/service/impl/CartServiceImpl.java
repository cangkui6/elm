package com.elm.cart.service.impl;

import com.elm.common.entity.Cart;
import com.elm.cart.mapper.CartMapper;
import com.elm.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CartServiceImpl implements CartService {

    @Autowired
    private CartMapper cartMapper;

    @Override
    public List<Cart> listCart(String userId, Integer businessId) {
        try {
            if (userId == null || userId.trim().isEmpty()) {
                log.warn("查询购物车失败：userId为空");
                return new ArrayList<>();
            }
            
            if (businessId == null) {
                log.warn("查询购物车失败：businessId为空");
                return new ArrayList<>();
            }
            
            List<Cart> result = cartMapper.listCart(userId, businessId);
            log.info("查询购物车: userId={}, businessId={}, 结果数量={}", userId, businessId, result != null ? result.size() : 0);
            return result != null ? result : new ArrayList<>();
        } catch (Exception e) {
            log.error("查询购物车异常: userId={}, businessId={}", userId, businessId, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Cart> listCartByUserId(String userId) {
        try {
            if (userId == null || userId.trim().isEmpty()) {
                log.warn("查询用户购物车失败：userId为空");
                return new ArrayList<>();
            }
            
            List<Cart> result = cartMapper.listCartByUserId(userId);
            log.info("查询用户购物车: userId={}, 结果数量={}", userId, result != null ? result.size() : 0);
            return result != null ? result : new ArrayList<>();
        } catch (Exception e) {
            log.error("查询用户购物车异常: userId={}", userId, e);
            return new ArrayList<>();
        }
    }

    @Override
    public int saveCart(Cart cart) {
        try {
            if (cart == null) {
                log.warn("保存购物车失败：购物车对象为空");
                return 0;
            }
            
            if (cart.getUserId() == null || cart.getUserId().trim().isEmpty()) {
                log.warn("保存购物车失败：userId为空");
                return 0;
            }
            
            if (cart.getBusinessId() == null) {
                log.warn("保存购物车失败：businessId为空");
                return 0;
            }
            
            if (cart.getFoodId() == null) {
                log.warn("保存购物车失败：foodId为空");
                return 0;
            }
            
            int result = cartMapper.saveCart(cart);
            log.info("保存购物车: userId={}, businessId={}, foodId={}, quantity={}, 结果={}",
                cart.getUserId(), cart.getBusinessId(), cart.getFoodId(), cart.getQuantity(), result);
            return result;
        } catch (Exception e) {
            log.error("保存购物车异常", e);
            return 0;
        }
    }

    @Override
    public int updateCart(Cart cart) {
        try {
            if (cart == null) {
                log.warn("更新购物车失败：购物车对象为空");
                return 0;
            }
            
            int result = cartMapper.updateCart(cart);
            log.info("更新购物车: userId={}, businessId={}, foodId={}, quantity={}, 结果={}",
                cart.getUserId(), cart.getBusinessId(), cart.getFoodId(), cart.getQuantity(), result);
            return result;
        } catch (Exception e) {
            log.error("更新购物车异常", e);
            return 0;
        }
    }

    @Override
    public int removeCart(String userId, Integer businessId, Integer foodId) {
        try {
            int result = cartMapper.removeCart(userId, businessId, foodId);
            log.info("删除购物车: userId={}, businessId={}, foodId={}, 结果={}", userId, businessId, foodId, result);
            return result;
        } catch (Exception e) {
            log.error("删除购物车异常", e);
            return 0;
        }
    }

    @Override
    public int removeCartByUserIdAndBusinessId(String userId, Integer businessId) {
        try {
            int result = cartMapper.removeCartByUserIdAndBusinessId(userId, businessId);
            log.info("删除用户商家购物车: userId={}, businessId={}, 结果={}", userId, businessId, result);
            return result;
        } catch (Exception e) {
            log.error("删除用户商家购物车异常", e);
            return 0;
        }
    }
} 