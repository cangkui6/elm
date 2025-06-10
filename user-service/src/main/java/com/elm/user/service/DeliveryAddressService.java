package com.elm.user.service;

import com.elm.common.entity.DeliveryAddress;

import java.util.List;

public interface DeliveryAddressService {

    DeliveryAddress getDeliveryAddressById(Integer daId);

    List<DeliveryAddress> listDeliveryAddressByUserId(String userId);

    int saveDeliveryAddress(DeliveryAddress deliveryAddress);

    int updateDeliveryAddress(DeliveryAddress deliveryAddress);

    int removeDeliveryAddress(Integer daId);
} 