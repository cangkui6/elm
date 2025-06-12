package com.elm.consumer.feign;

import com.elm.common.entity.Business;
import com.elm.common.result.ResponseResult;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Primary
public class BusinessClientFallback implements BusinessClient {

    @Override
    public ResponseResult<List<Business>> listBusinessByOrderTypeId(Integer orderTypeId, Integer categoryId, String userId) {
        return ResponseResult.error("商家服务不可用：获取商家列表失败");
    }

    @Override
    public ResponseResult<Business> getBusinessById(Integer businessId) {
        return ResponseResult.error("商家服务不可用：获取商家信息失败");
    }

    @Override
    public ResponseResult<List<Business>> listAllBusinesses() {
        return ResponseResult.error("商家服务不可用：获取所有商家失败");
    }
} 