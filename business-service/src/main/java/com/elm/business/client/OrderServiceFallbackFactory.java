package com.elm.business.client;

import com.elm.common.entity.Cart;
import com.elm.common.result.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class OrderServiceFallbackFactory implements FallbackFactory<OrderServiceClient> {

    @Override
    public OrderServiceClient create(Throwable cause) {
        log.error("Order service call failed", cause);
        return new OrderServiceClient() {
            @Override
            public ResponseResult<List<Cart>> listCart(Integer userId, Integer businessId) {
                return ResponseResult.error("服务降级：购物车服务调用失败");
            }
        };
    }
} 