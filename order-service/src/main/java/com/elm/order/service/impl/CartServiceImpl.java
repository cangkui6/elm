package com.elm.order.service.impl;

import com.elm.common.entity.Cart;
import com.elm.order.mapper.CartMapper;
import com.elm.order.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartMapper cartMapper;

    @Override
    public List<Cart> listCart(Integer userId, Integer businessId) {
        return cartMapper.listCart(userId, businessId);
    }

    @Override
    public List<Cart> listCartByUserId(Integer userId) {
        return cartMapper.listCartByUserId(userId);
    }

    @Override
    public int saveCart(Cart cart) {
        return cartMapper.saveCart(cart);
    }

    @Override
    public int updateCart(Cart cart) {
        return cartMapper.updateCart(cart);
    }

    @Override
    public int removeCart(Integer userId, Integer businessId, Integer foodId) {
        return cartMapper.removeCart(userId, businessId, foodId);
    }

    @Override
    public int removeCartByUserIdAndBusinessId(Integer userId, Integer businessId) {
        return cartMapper.removeCartByUserIdAndBusinessId(userId, businessId);
    }
} 