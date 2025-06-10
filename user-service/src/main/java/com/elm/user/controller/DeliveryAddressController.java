package com.elm.user.controller;

import com.elm.common.entity.DeliveryAddress;
import com.elm.common.result.ResponseResult;
import com.elm.user.service.DeliveryAddressService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/deliveryAddress")
@Slf4j
public class DeliveryAddressController {

    @Autowired
    private DeliveryAddressService deliveryAddressService;

    @GetMapping("/getDeliveryAddressById")
    @CircuitBreaker(name = "deliveryAddressService", fallbackMethod = "getDeliveryAddressByIdFallback")
    public ResponseResult<DeliveryAddress> getDeliveryAddressById(@RequestParam("daId") Integer daId) {
        DeliveryAddress deliveryAddress = deliveryAddressService.getDeliveryAddressById(daId);
        return ResponseResult.success(deliveryAddress);
    }

    @GetMapping("/listDeliveryAddressByUserId")
    @CircuitBreaker(name = "deliveryAddressService", fallbackMethod = "listDeliveryAddressByUserIdFallback")
    public ResponseResult<List<DeliveryAddress>> listDeliveryAddressByUserId(@RequestParam("userId") String userId) {
        List<DeliveryAddress> deliveryAddressList = deliveryAddressService.listDeliveryAddressByUserId(userId);
        return ResponseResult.success(deliveryAddressList);
    }

    @PostMapping("/saveDeliveryAddress")
    @CircuitBreaker(name = "deliveryAddressService", fallbackMethod = "saveDeliveryAddressFallbackWithParams")
    public ResponseResult<Integer> saveDeliveryAddress(
            @RequestParam("userId") String userId,
            @RequestParam("contactName") String contactName,
            @RequestParam("contactSex") String contactSex,
            @RequestParam("contactTel") String contactTel,
            @RequestParam("address") String address) {
        
        DeliveryAddress deliveryAddress = new DeliveryAddress();
        deliveryAddress.setUserId(userId);
        deliveryAddress.setContactName(contactName);
        deliveryAddress.setContactSex(contactSex);
        deliveryAddress.setContactTel(contactTel);
        deliveryAddress.setAddress(address);
        
        int result = deliveryAddressService.saveDeliveryAddress(deliveryAddress);
        if (result > 0) {
            return ResponseResult.success(deliveryAddress.getDaId());
        } else {
            return ResponseResult.error("添加地址失败");
        }
    }

    @PutMapping("/updateDeliveryAddress")
    @CircuitBreaker(name = "deliveryAddressService", fallbackMethod = "updateDeliveryAddressFallbackWithParams")
    public ResponseResult<Integer> updateDeliveryAddress(
            @RequestParam("daId") Integer daId,
            @RequestParam("userId") String userId,
            @RequestParam("contactName") String contactName,
            @RequestParam("contactSex") String contactSex,
            @RequestParam("contactTel") String contactTel,
            @RequestParam("address") String address) {
        
        DeliveryAddress deliveryAddress = new DeliveryAddress();
        deliveryAddress.setDaId(daId);
        deliveryAddress.setUserId(userId);
        deliveryAddress.setContactName(contactName);
        deliveryAddress.setContactSex(contactSex);
        deliveryAddress.setContactTel(contactTel);
        deliveryAddress.setAddress(address);
        
        int result = deliveryAddressService.updateDeliveryAddress(deliveryAddress);
        if (result > 0) {
            return ResponseResult.success(result);
        } else {
            return ResponseResult.error("更新地址失败");
        }
    }

    @DeleteMapping("/removeDeliveryAddress")
    @CircuitBreaker(name = "deliveryAddressService", fallbackMethod = "removeDeliveryAddressFallback")
    public ResponseResult<Integer> removeDeliveryAddress(@RequestParam("daId") Integer daId) {
        int result = deliveryAddressService.removeDeliveryAddress(daId);
        if (result > 0) {
            return ResponseResult.success(result);
        } else {
            return ResponseResult.error("删除地址失败");
        }
    }

    // Fallback methods
    public ResponseResult<DeliveryAddress> getDeliveryAddressByIdFallback(Integer daId, Throwable t) {
        log.error("Circuit breaker fallback: getDeliveryAddressById failed", t);
        return ResponseResult.error("服务降级：获取配送地址失败");
    }

    public ResponseResult<List<DeliveryAddress>> listDeliveryAddressByUserIdFallback(String userId, Throwable t) {
        log.error("Circuit breaker fallback: listDeliveryAddressByUserId failed", t);
        return ResponseResult.error("服务降级：获取用户配送地址列表失败");
    }

    public ResponseResult<Integer> saveDeliveryAddressFallback(DeliveryAddress deliveryAddress, Throwable t) {
        log.error("Circuit breaker fallback: saveDeliveryAddress failed", t);
        return ResponseResult.error("服务降级：添加配送地址失败");
    }
    
    public ResponseResult<Integer> saveDeliveryAddressFallbackWithParams(
            String userId, String contactName, String contactSex, 
            String contactTel, String address, Throwable t) {
        log.error("Circuit breaker fallback: saveDeliveryAddress failed", t);
        return ResponseResult.error("服务降级：添加配送地址失败");
    }

    public ResponseResult<Integer> updateDeliveryAddressFallback(DeliveryAddress deliveryAddress, Throwable t) {
        log.error("Circuit breaker fallback: updateDeliveryAddress failed", t);
        return ResponseResult.error("服务降级：更新配送地址失败");
    }

    public ResponseResult<Integer> updateDeliveryAddressFallbackWithParams(
            Integer daId, String userId, String contactName, 
            String contactSex, String contactTel, String address, Throwable t) {
        log.error("Circuit breaker fallback: updateDeliveryAddress failed", t);
        return ResponseResult.error("服务降级：更新配送地址失败");
    }

    public ResponseResult<Integer> removeDeliveryAddressFallback(Integer daId, Throwable t) {
        log.error("Circuit breaker fallback: removeDeliveryAddress failed", t);
        return ResponseResult.error("服务降级：删除配送地址失败");
    }
} 