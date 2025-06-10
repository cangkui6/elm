package com.elm.order.service;

import com.elm.common.entity.Cart;

import java.util.List;

public interface CartService {

    List<Cart> listCart(String userId, Integer businessId);

    List<Cart> listCartByUserId(String userId);

    int saveCart(Cart cart);

    int updateCart(Cart cart);

    int removeCart(String userId, Integer businessId, Integer foodId);

    int removeCartByUserIdAndBusinessId(String userId, Integer businessId);
} 