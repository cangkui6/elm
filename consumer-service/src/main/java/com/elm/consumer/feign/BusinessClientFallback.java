package com.elm.consumer.feign;

import com.elm.common.entity.Business;
import com.elm.common.result.ResponseResult;

import java.util.List;

public class BusinessClientFallback implements BusinessClient {

    @Override
    public ResponseResult<Business> getBusinessById(Integer businessId) {
        return ResponseResult.error("商家服務不可用：獲取商家信息失敗");
    }

    @Override
    public ResponseResult<List<Business>> listBusinessByOrderTypeId(Integer orderTypeId, Integer categoryId, String userId) {
        return ResponseResult.error("商家服務不可用：獲取商家列表失敗");
    }

    @Override
    public ResponseResult<List<Business>> listAllBusinesses() {
        return ResponseResult.error("商家服務不可用：獲取所有商家列表失敗");
    }
} 