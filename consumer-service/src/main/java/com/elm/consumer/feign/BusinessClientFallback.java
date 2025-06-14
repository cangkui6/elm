package com.elm.consumer.feign;

import com.elm.common.entity.Business;
import com.elm.common.result.ResponseResult;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BusinessClientFallback implements BusinessClient {

    @Override
    public ResponseResult<Business> getBusinessById(Integer businessId) {
        return ResponseResult.error("呃呃...服务器忙中...");
    }

    @Override
    public ResponseResult<List<Business>> listBusinessByOrderTypeId(Integer orderTypeId, Integer categoryId, String userId) {
        return ResponseResult.error("呃呃...服务器忙中...");
    }

    @Override
    public ResponseResult<List<Business>> listAllBusinesses() {
        return ResponseResult.error("呃呃...服务器忙中...");
    }
} 