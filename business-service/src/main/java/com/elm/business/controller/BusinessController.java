package com.elm.business.controller;

import com.elm.business.client.OrderServiceClient;
import com.elm.business.service.BusinessService;
import com.elm.business.service.FoodService;
import com.elm.common.entity.Business;
import com.elm.common.entity.Cart;
import com.elm.common.entity.Food;
import com.elm.common.result.ResponseResult;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/business")
@Slf4j
public class BusinessController {

    @Autowired
    private BusinessService businessService;
    
    @Autowired
    private OrderServiceClient orderServiceClient;
    
    @Autowired
    private FoodService foodService;

    @PostMapping("/listBusinessByOrderTypeId")
    @CircuitBreaker(name = "businessService", fallbackMethod = "listBusinessByOrderTypeIdFallback")
    public ResponseResult<List<Business>> listBusinessByOrderTypeId(
            @RequestParam(value = "orderTypeId", required = false) Integer orderTypeId, 
            @RequestParam(value = "categoryId", required = false) Integer categoryId) {
        
        // 打印收到的参数值，方便调试
        log.info("Received orderTypeId: {}, categoryId: {}", orderTypeId, categoryId);
        
        // 优先使用orderTypeId，如果为null则使用categoryId
        Integer typeId = orderTypeId != null ? orderTypeId : categoryId;
        
        // 如果两个参数都为null，返回所有商家
        if (typeId == null) {
            log.warn("Both orderTypeId and categoryId are null, returning all businesses");
            return ResponseResult.success(businessService.listAllBusinesses());
        }
        
        log.info("Querying businesses with orderTypeId: {}", typeId);
        try {
            List<Business> businessList = businessService.listBusinessByOrderTypeId(typeId);
            
            if (businessList == null || businessList.isEmpty()) {
                log.warn("No businesses found for orderTypeId: {}", typeId);
                return ResponseResult.error("没有找到该分类的商家");
            }
            
            log.info("Found {} businesses for orderTypeId: {}", businessList.size(), typeId);
            
            // Enhance with frontend-required fields
            businessList.forEach(business -> {
                business.setRating(4.8);
                business.setOrderCount(4);
                business.setMonthSales(345);
                business.setMinPrice(business.getStarPrice());
                business.setDeliveryMethod("蜂鸟配送");
                business.setDistance("3.22km");
                business.setDeliveryTime("30");
                business.setFoodType(business.getBusinessExplain());
                // Set random badge count for testing
                if (business.getBusinessId() % 3 == 0) {
                    business.setBadge(3);
                } else if (business.getBusinessId() % 3 == 1) {
                    business.setBadge(2);
                } else {
                    business.setBadge(1);
                }
            });
            
            return ResponseResult.success(businessList);
        } catch (Exception e) {
            log.error("Error querying businesses with orderTypeId: {}", typeId, e);
            return ResponseResult.error("查询商家列表时出错: " + e.getMessage());
        }
    }
    
    @PostMapping("/listBusinessByCategory")
    @CircuitBreaker(name = "businessService", fallbackMethod = "listBusinessByCategoryFallback")
    public ResponseResult<?> listBusinessByCategory(
            @RequestParam("categoryId") Integer categoryId,
            @RequestParam(value = "userId", required = false) Integer userId) {
        
        log.info("Querying businesses with categoryId: {}, userId: {}", categoryId, userId);
        
        try {
            // 使用categoryId查询商家列表
            List<Business> businessList = businessService.listBusinessByOrderTypeId(categoryId);
            
            if (businessList == null || businessList.isEmpty()) {
                log.warn("No businesses found for categoryId: {}", categoryId);
                return ResponseResult.error("没有找到该分类的商家");
            }
            
            log.info("Found {} businesses for categoryId: {}", businessList.size(), categoryId);
            
            // 增强商家信息
            businessList.forEach(business -> {
                business.setRating(4.8);
                business.setOrderCount(4);
                business.setMonthSales(345);
                business.setMinPrice(business.getStarPrice());
                business.setDeliveryMethod("蜂鸟配送");
                business.setDistance("3.22km");
                business.setDeliveryTime("30");
                business.setFoodType(business.getBusinessExplain());
                if (business.getBusinessId() % 3 == 0) {
                    business.setBadge(3);
                } else if (business.getBusinessId() % 3 == 1) {
                    business.setBadge(2);
                } else {
                    business.setBadge(1);
                }
            });
            
            // 如果用户已登录，查询购物车信息
            if (userId != null) {
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("businessList", businessList);
                
                // 查询该用户的购物车信息
                ResponseResult<List<Cart>> cartResponse = orderServiceClient.listCart(String.valueOf(userId), null);
                
                if (cartResponse.getCode() == 1) {
                    List<Cart> cartList = cartResponse.getData();
                    
                    // 统计每个商家的购物车商品数量
                    Map<Integer, Integer> businessCartCount = new HashMap<>();
                    if (cartList != null && !cartList.isEmpty()) {
                        for (Cart cart : cartList) {
                            Integer businessId = cart.getBusinessId();
                            businessCartCount.put(
                                businessId, 
                                businessCartCount.getOrDefault(businessId, 0) + cart.getQuantity()
                            );
                        }
                    }
                    
                    responseMap.put("businessCartCount", businessCartCount);
                }
                
                return ResponseResult.success(responseMap);
            }
            
            return ResponseResult.success(businessList);
            
        } catch (Exception e) {
            log.error("Error querying businesses with categoryId: {}", categoryId, e);
            return ResponseResult.error("查询商家列表时出错: " + e.getMessage());
        }
    }

    @PostMapping("/getBusinessById")
    @CircuitBreaker(name = "businessService", fallbackMethod = "getBusinessByIdFallback")
    public ResponseResult<Business> getBusinessById(@RequestParam("businessId") Integer businessId) {
        try {
            log.info("Querying business with businessId: {}", businessId);
            Business business = businessService.getBusinessById(businessId);
            
            if (business == null) {
                log.warn("No business found for businessId: {}", businessId);
                return ResponseResult.error("没有找到该商家");
            }
            
            // Enhance with frontend-required fields
            business.setRating(4.8);
            business.setOrderCount(4);
            business.setMonthSales(345);
            business.setMinPrice(business.getStarPrice());
            business.setDeliveryMethod("蜂鸟配送");
            business.setDistance("3.22km");
            business.setDeliveryTime("30");
            business.setFoodType(business.getBusinessExplain());
            
            return ResponseResult.success(business);
        } catch (Exception e) {
            log.error("Error querying business with businessId: {}", businessId, e);
            return ResponseResult.error("查询商家详情时出错: " + e.getMessage());
        }
    }

    @GetMapping("/listAllBusinesses")
    @CircuitBreaker(name = "businessService", fallbackMethod = "listAllBusinessesFallback")
    public ResponseResult<List<Business>> listAllBusinesses() {
        List<Business> businessList = businessService.listAllBusinesses();
        
        // Enhance with frontend-required fields
        businessList.forEach(business -> {
            business.setRating(4.8);
            business.setOrderCount(4);
            business.setMonthSales(345);
            business.setMinPrice(business.getStarPrice());
            business.setDeliveryMethod("蜂鸟配送");
            business.setDistance("3.22km");
            business.setDeliveryTime("30");
            business.setFoodType(business.getBusinessExplain());
            // Set random badge count
            if (business.getBusinessId() % 3 == 0) {
                business.setBadge(3);
            } else if (business.getBusinessId() % 3 == 1) {
                business.setBadge(2);
            } else {
                business.setBadge(1);
            }
        });
        
        return ResponseResult.success(businessList);
    }

    @GetMapping("/listAllBusinessesWithCartInfo")
    @CircuitBreaker(name = "businessService", fallbackMethod = "listAllBusinessesWithCartInfoFallback")
    public ResponseResult<?> listAllBusinessesWithCartInfo(@RequestParam(value = "userId", required = false) String userId) {
        try {
            log.info("Listing all businesses with cart info for userId: {}", userId);
            
            List<Business> businessList = businessService.listAllBusinesses();
            
            // Enhance with frontend-required fields
            businessList.forEach(business -> {
                business.setRating(4.8);
                business.setOrderCount(4);
                business.setMonthSales(345);
                business.setMinPrice(business.getStarPrice());
                business.setDeliveryMethod("蜂鸟配送");
                business.setDistance("3.22km");
                business.setDeliveryTime("30");
                business.setFoodType(business.getBusinessExplain());
                // Set random badge count
                if (business.getBusinessId() % 3 == 0) {
                    business.setBadge(3);
                } else if (business.getBusinessId() % 3 == 1) {
                    business.setBadge(2);
                } else {
                    business.setBadge(1);
                }
            });
            
            if (userId != null) {
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("businessList", businessList);
                
                // 查询该用户的购物车信息
                ResponseResult<List<Cart>> cartResponse = orderServiceClient.listCart(userId, null);
                
                if (cartResponse.getCode() == 1) {
                    List<Cart> cartList = cartResponse.getData();
                    
                    // 统计每个商家的购物车商品数量
                    Map<Integer, Integer> businessCartCount = new HashMap<>();
                    if (cartList != null && !cartList.isEmpty()) {
                        for (Cart cart : cartList) {
                            Integer businessId = cart.getBusinessId();
                            businessCartCount.put(
                                businessId, 
                                businessCartCount.getOrDefault(businessId, 0) + cart.getQuantity()
                            );
                        }
                    }
                    
                    responseMap.put("businessCartCount", businessCartCount);
                }
                
                return ResponseResult.success(responseMap);
            }
            
            return ResponseResult.success(businessList);
            
        } catch (Exception e) {
            log.error("Error querying businesses with cart info for userId: {}", userId, e);
            return ResponseResult.error("查询商家列表时出错: " + e.getMessage());
        }
    }

    @PostMapping("/getBusinessWithFoods")
    @CircuitBreaker(name = "businessService", fallbackMethod = "getBusinessWithFoodsFallback")
    public ResponseResult<?> getBusinessWithFoods(
            @RequestParam("businessId") Integer businessId,
            @RequestParam(value = "userId", required = false) String userId) {
        
        try {
            log.info("Querying business details with foods for businessId: {}, userId: {}", businessId, userId);
            
            // Get business details
            Business business = businessService.getBusinessById(businessId);
            if (business == null) {
                log.warn("No business found for businessId: {}", businessId);
                return ResponseResult.error("没有找到该商家");
            }
            
            // Enhance with frontend-required fields
            business.setRating(4.8);
            business.setOrderCount(4);
            business.setMonthSales(345);
            business.setMinPrice(business.getStarPrice());
            business.setDeliveryMethod("蜂鸟配送");
            business.setDistance("3.22km");
            business.setDeliveryTime("30");
            business.setFoodType(business.getBusinessExplain());
            
            // Get food list for this business
            List<Food> foodList = foodService.listFoodByBusinessId(businessId);
            
            // Prepare response
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("business", business);
            responseMap.put("foodList", foodList != null ? foodList : new ArrayList<>());
            
            // If user is logged in, get cart information
            if (userId != null && !userId.isEmpty()) {
                try {
                    // Get cart items for this user and business
                    ResponseResult<List<Cart>> cartResponse = orderServiceClient.listCart(userId, businessId);
                    
                    if (cartResponse.getCode() == 1 && cartResponse.getData() != null) {
                        List<Cart> cartList = cartResponse.getData();
                        
                        // Create a map of foodId to quantity
                        Map<Integer, Integer> foodQuantityMap = new HashMap<>();
                        double totalPrice = 0.0;
                        
                        for (Cart cart : cartList) {
                            foodQuantityMap.put(cart.getFoodId(), cart.getQuantity());
                            
                            // Find the corresponding food price and calculate
                            for (Food food : foodList) {
                                if (food.getFoodId().equals(cart.getFoodId())) {
                                    totalPrice += food.getFoodPrice().doubleValue() * cart.getQuantity();
                                    // Update the quantity in the food object
                                    food.setQuantity(cart.getQuantity());
                                    break;
                                }
                            }
                        }
                        
                        responseMap.put("foodQuantityMap", foodQuantityMap);
                        responseMap.put("totalPrice", totalPrice);
                    }
                } catch (Exception e) {
                    log.error("Error processing cart information for userId: {}", userId, e);
                }
            }
            
            return ResponseResult.success(responseMap);
            
        } catch (Exception e) {
            log.error("Error querying business with foods for businessId: {}, userId: {}", businessId, userId, e);
            return ResponseResult.error("查询商家详情时出错: " + e.getMessage());
        }
    }

    @PostMapping("/addFoodToCart")
    @CircuitBreaker(name = "businessService", fallbackMethod = "addFoodToCartFallback")
    public ResponseResult<?> addFoodToCart(
            @RequestParam("userId") String userId,
            @RequestParam("businessId") Integer businessId,
            @RequestParam("foodId") Integer foodId) {
        
        log.info("Adding food to cart: userId={}, businessId={}, foodId={}", userId, businessId, foodId);
        
        try {
            // Check if user is logged in
            if (userId == null || userId.isEmpty()) {
                return ResponseResult.error("请先登录");
            }
            
            // Call order service to add to cart
            ResponseResult<Integer> result = orderServiceClient.saveCart(userId, businessId, foodId);
            
            if (result.getCode() == 1) {
                // If successful, get updated cart information
                return getUpdatedCartInfo(userId, businessId);
            } else {
                return result;
            }
        } catch (NumberFormatException e) {
            log.error("Invalid userId format: {}", userId, e);
            return ResponseResult.error("用户ID格式不正确");
        } catch (Exception e) {
            log.error("Error adding food to cart", e);
            return ResponseResult.error("添加购物车失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/updateFoodInCart")
    @CircuitBreaker(name = "businessService", fallbackMethod = "updateFoodInCartFallback")
    public ResponseResult<?> updateFoodInCart(
            @RequestParam("userId") String userId,
            @RequestParam("businessId") Integer businessId,
            @RequestParam("foodId") Integer foodId,
            @RequestParam("quantity") Integer quantity) {
        
        log.info("Updating food in cart: userId={}, businessId={}, foodId={}, quantity={}", 
                userId, businessId, foodId, quantity);
        
        try {
            // Check if user is logged in
            if (userId == null || userId.isEmpty()) {
                return ResponseResult.error("请先登录");
            }
            
            // Call order service to update cart
            ResponseResult<Integer> result;
            
            if (quantity > 0) {
                // Update quantity
                result = orderServiceClient.updateCart(userId, businessId, foodId, quantity);
            } else {
                // Remove quantity
                result = orderServiceClient.removeCart(userId, businessId, foodId);
            }
            
            if (result.getCode() == 1) {
                // If successful, get updated cart information
                return getUpdatedCartInfo(userId, businessId);
            } else {
                return result;
            }
        } catch (NumberFormatException e) {
            log.error("Invalid userId format: {}", userId, e);
            return ResponseResult.error("用户ID格式不正确");
        } catch (Exception e) {
            log.error("Error updating food in cart", e);
            return ResponseResult.error("更新购物车失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/removeFoodFromCart")
    @CircuitBreaker(name = "businessService", fallbackMethod = "removeFoodFromCartFallback")
    public ResponseResult<?> removeFoodFromCart(
            @RequestParam("userId") String userId,
            @RequestParam("businessId") Integer businessId,
            @RequestParam("foodId") Integer foodId) {
        
        log.info("Removing food from cart: userId={}, businessId={}, foodId={}", userId, businessId, foodId);
        
        try {
            // Check if user is logged in
            if (userId == null || userId.isEmpty()) {
                return ResponseResult.error("请先登录");
            }
            
            // Call order service to remove from cart
            ResponseResult<Integer> result = orderServiceClient.removeCart(userId, businessId, foodId);
            
            if (result.getCode() == 1) {
                // If successful, get updated cart information
                return getUpdatedCartInfo(userId, businessId);
            } else {
                return result;
            }
        } catch (NumberFormatException e) {
            log.error("Invalid userId format: {}", userId, e);
            return ResponseResult.error("用户ID格式不正确");
        } catch (Exception e) {
            log.error("Error removing food from cart", e);
            return ResponseResult.error("删除购物车失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/getCartInfo")
    @CircuitBreaker(name = "businessService", fallbackMethod = "getCartInfoFallback")
    public ResponseResult<?> getCartInfo(
            @RequestParam("userId") String userId,
            @RequestParam("businessId") Integer businessId) {
        
        try {
            // Check if user is logged in
            if (userId == null || userId.isEmpty()) {
                return ResponseResult.error("请先登录");
            }
            
            return getUpdatedCartInfo(userId, businessId);
        } catch (NumberFormatException e) {
            log.error("Invalid userId format: {}", userId, e);
            return ResponseResult.error("用户ID格式不正确");
        } catch (Exception e) {
            log.error("Error getting cart info", e);
            return ResponseResult.error("获取购物车信息失败: " + e.getMessage());
        }
    }
    
    // Helper method to get updated cart information
    private ResponseResult<?> getUpdatedCartInfo(String userId, Integer businessId) {
        // Get food list for this business
        List<Food> foodList = foodService.listFoodByBusinessId(businessId);
        
        // Get cart items for this user and business
        ResponseResult<List<Cart>> cartResponse = orderServiceClient.listCart(userId, businessId);
        
        Map<String, Object> responseMap = new HashMap<>();
        
        if (cartResponse.getCode() == 1 && cartResponse.getData() != null) {
            List<Cart> cartList = cartResponse.getData();
            
            // Create a map of foodId to quantity
            Map<Integer, Integer> foodQuantityMap = new HashMap<>();
            double totalPrice = 0.0;
            
            for (Cart cart : cartList) {
                foodQuantityMap.put(cart.getFoodId(), cart.getQuantity());
                
                // Find the corresponding food price and calculate
                for (Food food : foodList) {
                    if (food.getFoodId().equals(cart.getFoodId())) {
                        totalPrice += food.getFoodPrice().doubleValue() * cart.getQuantity();
                        // Update the quantity in the food object
                        food.setQuantity(cart.getQuantity());
                        break;
                    }
                }
            }
            
            responseMap.put("foodList", foodList);
            responseMap.put("foodQuantityMap", foodQuantityMap);
            responseMap.put("totalPrice", totalPrice);
            responseMap.put("cartList", cartList);
        }
        
        return ResponseResult.success(responseMap);
    }

    // Fallback methods
    public ResponseResult<List<Business>> listBusinessByOrderTypeIdFallback(Integer orderTypeId, Integer categoryId, Throwable t) {
        log.error("Circuit breaker fallback: listBusinessByOrderTypeId failed", t);
        return ResponseResult.error("服务降级：获取商家列表失败");
    }
    
    public ResponseResult<?> listBusinessByCategoryFallback(Integer categoryId, Integer userId, Throwable t) {
        log.error("Circuit breaker fallback: listBusinessByCategory failed", t);
        return ResponseResult.error("服务降级：获取商家列表失败");
    }

    public ResponseResult<Business> getBusinessByIdFallback(Integer businessId, Throwable t) {
        log.error("Circuit breaker fallback: getBusinessById failed", t);
        return ResponseResult.error("服务降级：获取商家详情失败");
    }

    public ResponseResult<List<Business>> listAllBusinessesFallback(Throwable t) {
        log.error("Circuit breaker fallback: listAllBusinesses failed", t);
        return ResponseResult.error("服务降级：获取所有商家列表失败");
    }

    public ResponseResult<?> getBusinessWithFoodsFallback(Integer businessId, String userId, Throwable t) {
        log.error("Circuit breaker fallback: getBusinessWithFoods failed", t);
        return ResponseResult.error("服务降级：获取商家详情和食品列表失败");
    }
    
    public ResponseResult<?> addFoodToCartFallback(String userId, Integer businessId, Integer foodId, Throwable t) {
        log.error("Circuit breaker fallback: addFoodToCart failed", t);
        return ResponseResult.error("服务降级：添加购物车失败");
    }
    
    public ResponseResult<?> updateFoodInCartFallback(String userId, Integer businessId, Integer foodId, Integer quantity, Throwable t) {
        log.error("Circuit breaker fallback: updateFoodInCart failed", t);
        return ResponseResult.error("服务降级：更新购物车失败");
    }
    
    public ResponseResult<?> removeFoodFromCartFallback(String userId, Integer businessId, Integer foodId, Throwable t) {
        log.error("Circuit breaker fallback: removeFoodFromCart failed", t);
        return ResponseResult.error("服务降级：删除购物车失败");
    }
    
    public ResponseResult<?> getCartInfoFallback(String userId, Integer businessId, Throwable t) {
        log.error("Circuit breaker fallback: getCartInfo failed", t);
        return ResponseResult.error("服务降级：获取购物车信息失败");
    }

    public ResponseResult<?> listAllBusinessesWithCartInfoFallback(String userId, Throwable t) {
        log.error("Circuit breaker fallback: listAllBusinessesWithCartInfo failed", t);
        return ResponseResult.error("服务降级：获取所有商家列表失败");
    }
} 