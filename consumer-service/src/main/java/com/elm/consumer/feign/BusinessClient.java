package com.elm.consumer.feign;

import com.elm.common.entity.Business;
import com.elm.common.result.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "business-service", fallback = BusinessClientFallback.class)
public interface BusinessClient {

    @PostMapping("/business/listBusinessByOrderTypeId")
    ResponseResult<List<Business>> listBusinessByOrderTypeId(
            @RequestParam(value = "orderTypeId", required = false) Integer orderTypeId,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "userId", required = false) String userId);

    @PostMapping("/business/getBusinessById")
    ResponseResult<Business> getBusinessById(@RequestParam("businessId") Integer businessId);

    @GetMapping("/business/listAllBusinesses")
    ResponseResult<List<Business>> listAllBusinesses();
} 