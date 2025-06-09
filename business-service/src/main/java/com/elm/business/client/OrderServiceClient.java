package com.elm.business.client;

import com.elm.common.entity.Cart;
import com.elm.common.result.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "order-service", fallbackFactory = OrderServiceFallbackFactory.class)
public interface OrderServiceClient {

    @PostMapping("/cart/listCart")
    ResponseResult<List<Cart>> listCart(@RequestParam("userId") Integer userId, 
                                       @RequestParam(value = "businessId", required = false) Integer businessId);
} 