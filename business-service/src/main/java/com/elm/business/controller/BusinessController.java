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

    // 通用方法判断响应是否成功
    private boolean isResponseSuccessful(ResponseResult<?> response) {
        return response != null && 
               (response.getCode() == 1 || response.getCode() == 200) && 
               response.getData() != null;
    }

    @PostMapping("/listBusinessByOrderTypeId")
    @CircuitBreaker(name = "businessService", fallbackMethod = "listBusinessByOrderTypeIdFallback")
    public ResponseResult<List<Business>> listBusinessByOrderTypeId(
            @RequestParam(value = "orderTypeId", required = false) Integer orderTypeId, 
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "userId", required = false) String userId) {
        
        // 打印收到的参数值，方便调试
        log.info("Received orderTypeId: {}, categoryId: {}, userId: {}", orderTypeId, categoryId, userId);
        
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
            
            // 仅初始化购物车数量
            businessList.forEach(business -> {
                business.setMinPrice(business.getStarPrice());
                // 初始化购物车数量为0
                business.setQuantity(0);
            });
            
            // 如果用户已登录，查询购物车信息并设置数量
            if (userId != null && !userId.isEmpty()) {
                log.info("User logged in, fetching cart information for userId: {}", userId);
                
                try {
                // 查询该用户的所有购物车信息
                ResponseResult<List<Cart>> cartResponse = orderServiceClient.listCart(userId, null);
                
                if (isResponseSuccessful(cartResponse) && cartResponse.getData() != null) {
                    List<Cart> cartList = cartResponse.getData();
                    log.info("Found {} items in cart for userId: {}", cartList.size(), userId);
                    
                    // 统计每个商家的购物车商品数量并设置到对应的商家对象
                    Map<Integer, Integer> businessCartCount = new HashMap<>();
                    for (Cart cart : cartList) {
                        Integer businessId = cart.getBusinessId();
                        Integer currentCount = businessCartCount.getOrDefault(businessId, 0);
                        Integer newCount = currentCount + cart.getQuantity();
                        businessCartCount.put(businessId, newCount);
                    }
                    
                    // 设置每个商家的购物车商品总数量
                    for (Business business : businessList) {
                        Integer quantity = businessCartCount.get(business.getBusinessId());
                            if (quantity != null && quantity > 0) {
                            business.setQuantity(quantity);
                            log.info("设置商家 {} ({}) 的购物车数量为: {}", 
                                business.getBusinessId(), business.getBusinessName(), quantity);
                        }
                    }
                } else {
                    log.warn("未能获取到用户 {} 的购物车信息或购物车为空", userId);
                    }
                } catch (Exception e) {
                    log.error("查询用户 {} 的购物车信息失败: {}", userId, e.getMessage());
                    // 即使查询购物车失败，也继续返回商家列表，只是不带购物车信息
                }
            }
            
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
            
            // 仅初始化商家必要字段
            businessList.forEach(business -> {
                business.setMinPrice(business.getStarPrice());
            });
            
            // 如果用户已登录，查询购物车信息
            if (userId != null) {
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("businessList", businessList);
                
                // 查询该用户的购物车信息
                ResponseResult<List<Cart>> cartResponse = orderServiceClient.listCart(String.valueOf(userId), null);
                
                if (isResponseSuccessful(cartResponse)) {
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
            
            // 仅设置必要字段
            business.setMinPrice(business.getStarPrice());
            
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
        
        // 仅初始化必要字段
        businessList.forEach(business -> {
            business.setMinPrice(business.getStarPrice());
        });
        
        return ResponseResult.success(businessList);
    }

    @GetMapping("/listAllBusinessesWithCartInfo")
    @CircuitBreaker(name = "businessService", fallbackMethod = "listAllBusinessesWithCartInfoFallback")
    public ResponseResult<?> listAllBusinessesWithCartInfo(@RequestParam(value = "userId", required = false) String userId) {
        try {
            log.info("Listing all businesses with cart info for userId: {}", userId);
            
            List<Business> businessList = businessService.listAllBusinesses();
            
            // 仅初始化必要字段
            businessList.forEach(business -> {
                business.setMinPrice(business.getStarPrice());
            });
            
            if (userId != null) {
                try {
                    // 设置购物车商品数量到business对象上，方便前端直接使用
                    // 查询该用户的所有购物车信息
                ResponseResult<List<Cart>> cartResponse = orderServiceClient.listCart(userId, null);
                
                    if (cartResponse.getCode() == 1 && cartResponse.getData() != null) {
                    List<Cart> cartList = cartResponse.getData();
                    
                        // 统计每个商家的购物车商品数量并设置到对应的商家对象
                    Map<Integer, Integer> businessCartCount = new HashMap<>();
                        for (Cart cart : cartList) {
                            Integer businessId = cart.getBusinessId();
                            Integer currentCount = businessCartCount.getOrDefault(businessId, 0);
                            Integer newCount = currentCount + cart.getQuantity();
                            businessCartCount.put(businessId, newCount);
                        }
                        
                        // 设置每个商家的购物车商品总数量
                        for (Business business : businessList) {
                            Integer quantity = businessCartCount.get(business.getBusinessId());
                            if (quantity != null && quantity > 0) {
                                business.setQuantity(quantity);
                                log.info("设置商家 {} ({}) 的购物车数量为: {}", 
                                    business.getBusinessId(), business.getBusinessName(), quantity);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("查询用户 {} 的购物车信息失败: {}", userId, e.getMessage());
                    // 即使查询购物车失败，也继续返回商家列表，只是不带购物车信息
                }
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
            
            // 仅设置必要字段
            business.setMinPrice(business.getStarPrice());
            
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
                    
                    if (isResponseSuccessful(cartResponse)) {
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
            
            if (isResponseSuccessful(result)) {
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
            
            if (isResponseSuccessful(result)) {
                // If successful, get updated cart information
                return getUpdatedCartInfo(userId, businessId);
            } else {
                return result;
            }
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
            
            if (isResponseSuccessful(result)) {
                // If successful, get updated cart information
                return getUpdatedCartInfo(userId, businessId);
            } else {
                return result;
            }
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
        
        log.info("Getting cart info: userId={}, businessId={}", userId, businessId);
        
        try {
            // Check if user is logged in
            if (userId == null || userId.isEmpty()) {
                return ResponseResult.error("请先登录");
            }
            
            return getUpdatedCartInfo(userId, businessId);
        } catch (Exception e) {
            log.error("Error getting cart info", e);
            return ResponseResult.error("获取购物车信息失败: " + e.getMessage());
        }
    }
    
    // Helper method to get updated cart information
    private ResponseResult<?> getUpdatedCartInfo(String userId, Integer businessId) {
        try {
            // Get updated cart information
        ResponseResult<List<Cart>> cartResponse = orderServiceClient.listCart(userId, businessId);
        
            if (!isResponseSuccessful(cartResponse)) {
                return ResponseResult.error("获取购物车信息失败");
            }
        
            List<Cart> cartList = cartResponse.getData();
            
            // Create a map of foodId to quantity and calculate total price
            Map<Integer, Integer> foodQuantityMap = new HashMap<>();
            double totalPrice = 0.0;
            
            for (Cart cart : cartList) {
                foodQuantityMap.put(cart.getFoodId(), cart.getQuantity());
                
                // Get food price
                Food food = cart.getFood();
                if (food != null && food.getFoodPrice() != null) {
                        totalPrice += food.getFoodPrice().doubleValue() * cart.getQuantity();
                }
            }
            
            // Prepare response
            Map<String, Object> result = new HashMap<>();
            result.put("foodQuantityMap", foodQuantityMap);
            result.put("totalPrice", totalPrice);
            
            return ResponseResult.success(result);
        } catch (Exception e) {
            log.error("Error getting updated cart info", e);
            return ResponseResult.error("获取购物车信息失败: " + e.getMessage());
        }
    }

    /**
     * 专门用于查询用户在各个商家的购物车数量
     * @param userId 用户ID
     * @return 商家ID到购物车数量的映射
     */
    @PostMapping("/getCartQuantityMap")
    @CircuitBreaker(name = "businessService", fallbackMethod = "getCartQuantityMapFallback")
    public ResponseResult<?> getCartQuantityMap(@RequestParam("userId") String userId) {
        try {
            log.info("查询用户 {} 的各商家购物车数量", userId);
            
            if (userId == null || userId.isEmpty()) {
                return ResponseResult.error("用户ID不能为空");
            }
            
            // 查询该用户的所有购物车信息
            ResponseResult<List<Cart>> cartResponse = orderServiceClient.listCart(userId, null);
            
            // 详细记录返回值信息，帮助诊断
            if (cartResponse != null) {
                log.info("购物车服务响应状态: code={}, message={}", cartResponse.getCode(), cartResponse.getMessage());
                
                // 处理成功状态（code=200或code=1）
                if ((cartResponse.getCode() == 200 || cartResponse.getCode() == 1) && cartResponse.getData() != null) {
                    List<Cart> cartList = cartResponse.getData();
                    log.info("购物车记录数量: {}", cartList.size());
                    
                    if (!cartList.isEmpty()) {
                        // 打印所有购物车记录进行样本检查
                        for (Cart cart : cartList) {
                            log.info("购物车记录: cartId={}, userId={}, businessId={}, foodId={}, quantity={}", 
                                cart.getCartId(), cart.getUserId(), cart.getBusinessId(), 
                                cart.getFoodId(), cart.getQuantity());
                        }
                        
                        // 统计每个商家的购物车商品数量
                        Map<Integer, Integer> businessCartCount = new HashMap<>();
                        for (Cart cart : cartList) {
                            Integer businessId = cart.getBusinessId();
                            if (businessId != null) {
                                Integer currentCount = businessCartCount.getOrDefault(businessId, 0);
                                Integer quantity = cart.getQuantity() != null ? cart.getQuantity() : 0;
                                Integer newCount = currentCount + quantity;
                                businessCartCount.put(businessId, newCount);
                                log.info("累加购物车: 商家ID={}, 当前数量={}, 新增数量={}, 新总数={}", 
                                    businessId, currentCount, quantity, newCount);
                            }
                        }
                        
                        // 打印统计结果
                        if (!businessCartCount.isEmpty()) {
                            log.info("购物车统计结果: {}", businessCartCount);
                            return ResponseResult.success(businessCartCount);
                        } else {
                            log.warn("购物车统计结果为空 (可能是businessId为null)");
                            return ResponseResult.success(new HashMap<>());
                        }
                    } else {
                        log.info("购物车为空");
                        return ResponseResult.success(new HashMap<>());
                    }
                } else {
                    // 处理失败状态
                    log.warn("购物车服务返回错误: code={}, message={}", 
                        cartResponse.getCode(), cartResponse.getMessage());
                    return ResponseResult.success(new HashMap<>());
                }
            } else {
                log.warn("购物车服务响应为null");
                return ResponseResult.success(new HashMap<>());
            }
        } catch (Exception e) {
            log.error("查询用户 {} 的购物车数量失败", userId, e);
            return ResponseResult.success(new HashMap<>()); // 即使出错也返回空Map而不是错误
        }
    }
    
    /**
     * getCartQuantityMap的降级方法
     */
    public ResponseResult<?> getCartQuantityMapFallback(String userId, Throwable t) {
        log.error("查询用户 {} 的购物车数量服务降级", userId, t);
        return ResponseResult.error("服务降级：查询购物车数量失败");
    }

    // Fallback methods
    public ResponseResult<List<Business>> listBusinessByOrderTypeIdFallback(Integer orderTypeId, Integer categoryId, String userId, Throwable t) {
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

    /**
     * 查询数据库中所有商家信息
     */
    @GetMapping("/getAllBusinessesFromDb")
    public ResponseResult<?> getAllBusinessesFromDb() {
        try {
            // 查询数据库中所有商家
            List<Business> businessList = businessService.listAllBusinesses();
            
            // 统计每个分类下的商家数量
            Map<Integer, Integer> categoryCountMap = new HashMap<>();
            for (Business business : businessList) {
                Integer typeId = business.getOrderTypeId();
                categoryCountMap.put(typeId, categoryCountMap.getOrDefault(typeId, 0) + 1);
            }
            
            // 创建响应
            Map<String, Object> result = new HashMap<>();
            result.put("totalCount", businessList.size());
            result.put("businessList", businessList);
            result.put("categoryCountMap", categoryCountMap);
            
            return ResponseResult.success(result);
        } catch (Exception e) {
            log.error("查询所有商家失败", e);
            return ResponseResult.error("查询所有商家失败: " + e.getMessage());
        }
    }
} 