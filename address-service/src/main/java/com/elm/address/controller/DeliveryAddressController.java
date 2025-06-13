package com.elm.address.controller;

import com.elm.common.entity.DeliveryAddress;
import com.elm.common.result.ResponseResult;
import com.elm.address.service.DeliveryAddressService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/deliveryAddress")
@Slf4j
public class DeliveryAddressController {

    @Autowired
    private DeliveryAddressService deliveryAddressService;

    @GetMapping("/getDeliveryAddressById")
    @CircuitBreaker(name = "deliveryAddressService", fallbackMethod = "getDeliveryAddressByIdFallback")
    @RateLimiter(name = "deliveryAddressService")
    @Retry(name = "deliveryAddressService")
    public ResponseResult<DeliveryAddress> getDeliveryAddressById(@RequestParam("daId") Integer daId) {
        log.info("Getting delivery address by id: {}", daId);
        DeliveryAddress deliveryAddress = deliveryAddressService.getDeliveryAddressById(daId);
        return ResponseResult.success(deliveryAddress);
    }

    @GetMapping("/listDeliveryAddressByUserId")
    @CircuitBreaker(name = "deliveryAddressService", fallbackMethod = "listDeliveryAddressByUserIdFallback")
    @RateLimiter(name = "deliveryAddressService")
    @Retry(name = "deliveryAddressService")
    public ResponseResult<List<DeliveryAddress>> listDeliveryAddressByUserId(@RequestParam("userId") String userId) {
        log.info("Getting delivery address list for user: {}", userId);
        List<DeliveryAddress> deliveryAddressList = deliveryAddressService.listDeliveryAddressByUserId(userId);
        return ResponseResult.success(deliveryAddressList);
    }

    @PostMapping("/saveDeliveryAddress")
    @CircuitBreaker(name = "deliveryAddressService", fallbackMethod = "saveDeliveryAddressFallbackWithParams")
    @RateLimiter(name = "deliveryAddressService")
    @Retry(name = "deliveryAddressService")
    public ResponseResult<Integer> saveDeliveryAddress(
            @RequestParam("userId") String userId,
            @RequestParam("contactName") String contactName,
            @RequestParam("contactSex") String contactSex,
            @RequestParam("contactTel") String contactTel,
            @RequestParam("address") String address) {
        
        log.info("Saving delivery address for user: {}", userId);
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
    @RateLimiter(name = "deliveryAddressService")
    @Retry(name = "deliveryAddressService")
    public ResponseResult<Integer> updateDeliveryAddress(
            @RequestParam("daId") Integer daId,
            @RequestParam("userId") String userId,
            @RequestParam("contactName") String contactName,
            @RequestParam("contactSex") String contactSex,
            @RequestParam("contactTel") String contactTel,
            @RequestParam("address") String address) {
        
        log.info("Updating delivery address: {}", daId);
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
    @RateLimiter(name = "deliveryAddressService")
    @Retry(name = "deliveryAddressService")
    public ResponseResult<Integer> removeDeliveryAddress(@RequestParam("daId") Integer daId) {
        log.info("Removing delivery address: {}", daId);
        int result = deliveryAddressService.removeDeliveryAddress(daId);
        if (result > 0) {
            return ResponseResult.success(result);
        } else {
            return ResponseResult.error("删除地址失败");
        }
    }

    // 添加页面级别的降级处理端点
    @GetMapping("/page/getDeliveryAddressById")
    @CircuitBreaker(name = "deliveryAddressService", fallbackMethod = "redirectToFallbackPage")
    public ModelAndView getDeliveryAddressByIdPage(@RequestParam("daId") Integer daId, HttpServletRequest request) {
        // 这个方法可能会重定向到错误页面
        DeliveryAddress deliveryAddress = deliveryAddressService.getDeliveryAddressById(daId);
        ModelAndView mv = new ModelAndView("address-detail");
        mv.addObject("address", deliveryAddress);
        return mv;
    }

    // Fallback methods for API endpoints
    public ResponseResult<DeliveryAddress> getDeliveryAddressByIdFallback(Integer daId, Throwable t) {
        log.error("Circuit breaker fallback: getDeliveryAddressById failed for daId: {}", daId, t);
        return ResponseResult.error("服务降级：获取配送地址失败，请稍后重试");
    }

    public ResponseResult<List<DeliveryAddress>> listDeliveryAddressByUserIdFallback(String userId, Throwable t) {
        log.error("Circuit breaker fallback: listDeliveryAddressByUserId failed for userId: {}", userId, t);
        return ResponseResult.error("服务降级：获取用户配送地址列表失败，请稍后重试");
    }

    public ResponseResult<Integer> saveDeliveryAddressFallbackWithParams(
            String userId, String contactName, String contactSex, 
            String contactTel, String address, Throwable t) {
        log.error("Circuit breaker fallback: saveDeliveryAddress failed for userId: {}", userId, t);
        return ResponseResult.error("服务降级：添加配送地址失败，请稍后重试");
    }

    public ResponseResult<Integer> updateDeliveryAddressFallbackWithParams(
            Integer daId, String userId, String contactName, 
            String contactSex, String contactTel, String address, Throwable t) {
        log.error("Circuit breaker fallback: updateDeliveryAddress failed for daId: {}", daId, t);
        return ResponseResult.error("服务降级：更新配送地址失败，请稍后重试");
    }

    public ResponseResult<Integer> removeDeliveryAddressFallback(Integer daId, Throwable t) {
        log.error("Circuit breaker fallback: removeDeliveryAddress failed for daId: {}", daId, t);
        return ResponseResult.error("服务降级：删除配送地址失败，请稍后重试");
    }

    // Page-level fallback method that redirects to error page
    public ModelAndView redirectToFallbackPage(Integer daId, HttpServletRequest request, Throwable t) {
        log.error("Page-level circuit breaker fallback triggered for daId: {}", daId, t);
        
        String originalUrl = request.getRequestURL().toString();
        if (request.getQueryString() != null) {
            originalUrl += "?" + request.getQueryString();
        }
        
        try {
            String fallbackUrl = String.format("/fallback/address-service?originalUrl=%s&errorType=%s&errorMessage=%s",
                    URLEncoder.encode(originalUrl, StandardCharsets.UTF_8),
                    URLEncoder.encode("CIRCUIT_BREAKER", StandardCharsets.UTF_8),
                    URLEncoder.encode("配送地址服务暂时不可用，已启动熔断保护", StandardCharsets.UTF_8));
            
            ModelAndView mv = new ModelAndView("redirect:" + fallbackUrl);
            return mv;
        } catch (Exception e) {
            log.error("Error building fallback URL", e);
            return new ModelAndView("redirect:/fallback/address-service");
        }
    }

    // 健康检查端点
    @GetMapping("/health")
    public ResponseResult<String> healthCheck() {
        return ResponseResult.success("Address service is healthy");
    }

    // 测试熔断器的端点（仅用于测试）
    @GetMapping("/test/trigger-circuit-breaker")
    @CircuitBreaker(name = "deliveryAddressService", fallbackMethod = "testFallback")
    public ResponseResult<String> testCircuitBreaker(@RequestParam(value = "shouldFail", defaultValue = "false") boolean shouldFail) {
        if (shouldFail) {
            throw new RuntimeException("测试异常：触发熔断器");
        }
        return ResponseResult.success("测试成功");
    }

    public ResponseResult<String> testFallback(boolean shouldFail, Throwable t) {
        log.warn("Test circuit breaker triggered fallback", t);
        return ResponseResult.error("熔断器测试：服务降级处理");
    }
} 