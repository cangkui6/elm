package com.elm.order.service;

import com.elm.common.entity.Cart;

import java.util.List;

public interface CartService {

    List<Cart> listCart(Integer userId, Integer businessId);

    List<Cart> listCartByUserId(Integer userId);

    int saveCart(Cart cart);

    int updateCart(Cart cart);

    int removeCart(Integer userId, Integer businessId, Integer foodId);

    int removeCartByUserIdAndBusinessId(Integer userId, Integer businessId);
} 