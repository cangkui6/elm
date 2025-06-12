package com.elm.consumer.feign;

import com.elm.common.entity.DeliveryAddress;
import com.elm.common.result.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "address-service", fallback = AddressClientFallback.class)
public interface AddressClient {

    @GetMapping("/deliveryAddress/getDeliveryAddressById")
    ResponseResult<DeliveryAddress> getDeliveryAddressById(@RequestParam("daId") Integer daId);

    @GetMapping("/deliveryAddress/listDeliveryAddressByUserId")
    ResponseResult<List<DeliveryAddress>> listDeliveryAddressByUserId(@RequestParam("userId") String userId);

    @PostMapping("/deliveryAddress/saveDeliveryAddress")
    ResponseResult<Integer> saveDeliveryAddress(
            @RequestParam("userId") String userId,
            @RequestParam("contactName") String contactName,
            @RequestParam("contactSex") String contactSex,
            @RequestParam("contactTel") String contactTel,
            @RequestParam("address") String address);

    @PutMapping("/deliveryAddress/updateDeliveryAddress")
    ResponseResult<Integer> updateDeliveryAddress(
            @RequestParam("daId") Integer daId,
            @RequestParam("userId") String userId,
            @RequestParam("contactName") String contactName,
            @RequestParam("contactSex") String contactSex,
            @RequestParam("contactTel") String contactTel,
            @RequestParam("address") String address);

    @DeleteMapping("/deliveryAddress/removeDeliveryAddress")
    ResponseResult<Integer> removeDeliveryAddress(@RequestParam("daId") Integer daId);
} 