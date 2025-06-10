package com.elm.order.controller;

import com.elm.common.entity.Cart;
import com.elm.common.entity.Order;
import com.elm.common.entity.OrderDetail;
import com.elm.common.entity.Business;
import com.elm.common.entity.Food;
import com.elm.common.result.ResponseResult;
import com.elm.order.mapper.CartMapper;
import com.elm.order.mapper.FoodMapper;
import com.elm.order.mapper.OrderMapper;
import com.elm.order.service.CartService;
import com.elm.order.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private FoodMapper foodMapper;
    
    @Autowired
    private OrderMapper orderMapper;

    /**
     * 创建订单
     * @param requestMap 包含订单信息和订单明细列表的Map
     * @return 订单ID
     */
    @PostMapping("/createOrder")
    @CircuitBreaker(name = "orderService", fallbackMethod = "createOrderFallback")
    public ResponseResult<Integer> createOrder(@RequestBody Map<String, Object> requestMap) {
        try {
            // 从请求中获取订单信息
            Order order = new Order();
            order.setUserId((String) requestMap.get("userId"));
            order.setBusinessId((Integer) requestMap.get("businessId"));
            order.setDaId((Integer) requestMap.get("daId"));
            order.setOrderTotal(Double.parseDouble(requestMap.get("orderTotal").toString()));
            
            // 设置订单日期
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            order.setOrderDate(sdf.format(new Date()));
            
            // 设置订单状态为未支付
            order.setOrderState(0);
            
            // 从请求中获取订单明细列表
            List<Map<String, Object>> detailMapList = (List<Map<String, Object>>) requestMap.get("orderDetailList");
            List<OrderDetail> orderDetailList = new ArrayList<>();
            
            for (Map<String, Object> detailMap : detailMapList) {
                OrderDetail detail = new OrderDetail();
                detail.setFoodId((Integer) detailMap.get("foodId"));
                detail.setQuantity((Integer) detailMap.get("quantity"));
                orderDetailList.add(detail);
            }
            
            Integer orderId = orderService.createOrder(order, orderDetailList);
            if (orderId != null) {
                return ResponseResult.success(orderId);
            } else {
                return ResponseResult.error("创建订单失败");
            }
        } catch (Exception e) {
            log.error("创建订单异常", e);
            return ResponseResult.error("创建订单异常：" + e.getMessage());
        }
    }
    
    /**
     * 从购物车创建订单的合成方法
     * @param userId 用户ID
     * @param businessId 商家ID
     * @param daId 送货地址ID
     * @param orderTotal 订单总价
     * @return 订单ID
     */
    @PostMapping("/createOrders")
    @CircuitBreaker(name = "orderService", fallbackMethod = "createOrdersFallback")
    public ResponseResult<Integer> createOrders(
            @RequestParam("userId") String userId, 
            @RequestParam("businessId") Integer businessId,
            @RequestParam("daId") Integer daId,
            @RequestParam("orderTotal") Double orderTotal) {
        try {
            log.info("创建订单请求: userId={}, businessId={}, daId={}, orderTotal={}", 
                    userId, businessId, daId, orderTotal);
            
            // 1. 创建订单对象
            Order order = new Order();
            order.setUserId(userId);
            order.setBusinessId(businessId);
            order.setDaId(daId);
            order.setOrderTotal(orderTotal);
            
            // 设置订单日期
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            order.setOrderDate(sdf.format(new Date()));
            
            // 设置订单状态为未支付
            order.setOrderState(0);
            
            // 2. 获取用户的购物车数据
            List<Cart> cartList = cartService.listCart(userId, businessId);
            if (cartList == null || cartList.isEmpty()) {
                return ResponseResult.error("购物车为空，无法创建订单");
            }
            
            log.info("购物车数据: {}", cartList);
            
            // 3. 将购物车数据转换为订单明细
            List<OrderDetail> orderDetailList = new ArrayList<>();
            for (Cart cart : cartList) {
                OrderDetail detail = new OrderDetail();
                detail.setFoodId(cart.getFoodId());
                detail.setQuantity(cart.getQuantity());
                orderDetailList.add(detail);
            }
            
            // 4. 创建订单及订单明细
            Integer orderId = orderService.createOrder(order, orderDetailList);
            
            if (orderId != null) {
                log.info("订单创建成功: orderId={}", orderId);
                return ResponseResult.success(orderId);
            } else {
                return ResponseResult.error("创建订单失败");
            }
        } catch (Exception e) {
            log.error("创建订单异常", e);
            return ResponseResult.error("创建订单异常：" + e.getMessage());
        }
    }

    /**
     * 根据订单ID获取订单信息
     * @param orderId 订单ID
     * @return 订单信息
     */
    @GetMapping("/getOrderById")
    @CircuitBreaker(name = "orderService", fallbackMethod = "getOrderByIdFallback")
    public ResponseResult<Order> getOrderById(@RequestParam("orderId") Integer orderId) {
        Order order = orderService.getOrderById(orderId);
        if (order != null) {
            return ResponseResult.success(order);
        } else {
            return ResponseResult.error("订单不存在");
        }
    }

    /**
     * 根据用户ID获取订单列表
     * @param userId 用户ID
     * @return 订单列表
     */
    @GetMapping("/listOrdersByUserId")
    @CircuitBreaker(name = "orderService", fallbackMethod = "listOrdersByUserIdFallback")
    public ResponseResult<List<Order>> listOrdersByUserId(@RequestParam("userId") String userId) {
        List<Order> orderList = orderService.listOrdersByUserId(userId);
        return ResponseResult.success(orderList);
    }
    
    /**
     * 根据用户ID获取订单列表 (POST方法版本)
     * @param userId 用户ID
     * @return 订单列表
     */
    @PostMapping("/listOrdersByUserId")
    @CircuitBreaker(name = "orderService", fallbackMethod = "listOrdersByUserIdFallback")
    public ResponseResult<List<Order>> listOrdersByUserIdPost(@RequestParam("userId") String userId) {
        log.info("POST请求：获取用户订单列表，userId: {}", userId);
        try {
            List<Order> orderList = orderService.listOrdersByUserId(userId);
            log.info("获取到用户订单数量: {}", orderList != null ? orderList.size() : 0);
            
            // 详细日志记录每个订单的关键信息，便于调试
            if (orderList != null) {
                for (int i = 0; i < orderList.size(); i++) {
                    Order order = orderList.get(i);
                    log.info("订单[{}]信息: orderId={}, businessName={}, orderState={}, orderTotal={}, list大小={}",
                             i, order.getOrderId(), 
                             order.getBusiness() != null ? order.getBusiness().getBusinessName() : "null",
                             order.getOrderState(),
                             order.getOrderTotal(),
                             order.getList() != null ? order.getList().size() : "null");
                }
            }
            
            return ResponseResult.success(orderList);
        } catch (Exception e) {
            log.error("获取用户订单列表异常: ", e);
            return ResponseResult.error("获取订单列表失败: " + e.getMessage());
        }
    }

    /**
     * 更新订单状态
     * @param orderId 订单ID
     * @param orderState 订单状态
     * @return 更新结果
     */
    @PutMapping("/updateOrderState")
    @CircuitBreaker(name = "orderService", fallbackMethod = "updateOrderStateFallback")
    public ResponseResult<Integer> updateOrderState(@RequestParam("orderId") Integer orderId, @RequestParam("orderState") Integer orderState) {
        Integer result = orderService.updateOrderState(orderId, orderState);
        if (result > 0) {
            return ResponseResult.success(result);
        } else {
            return ResponseResult.error("更新订单状态失败");
        }
    }

    /**
     * 根据订单ID获取订单信息，包括商家和食品明细，用于支付页面
     * @param orderId 订单ID
     * @return 订单信息、商家信息和订单明细
     */
    @PostMapping("/getOrdersById")
    @CircuitBreaker(name = "orderService", fallbackMethod = "getOrdersByIdFallback")
    public ResponseResult<Map<String, Object>> getOrdersById(@RequestParam("orderId") Integer orderId) {
        try {
            log.info("获取订单信息请求：orderId={}", orderId);
            
            if (orderId == null) {
                log.error("订单ID为空");
                return ResponseResult.error("订单ID不能为空");
            }
            
            // 1. 获取订单信息（包含商家信息和订单明细）
            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                log.warn("订单不存在：orderId={}", orderId);
                return ResponseResult.error("订单不存在");
            }
            
            log.info("获取到订单信息：orderId={}, businessName={}, orderTotal={}",
                    order.getOrderId(), 
                    order.getBusiness() != null ? order.getBusiness().getBusinessName() : "未知",
                    order.getOrderTotal());
            
            // 2. 创建返回结果Map
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("orderId", order.getOrderId());
            resultMap.put("orderDate", order.getOrderDate());
            resultMap.put("orderTotal", order.getOrderTotal());
            resultMap.put("orderState", order.getOrderState());
            
            // 3. 设置商家信息
            if (order.getBusiness() != null) {
                resultMap.put("business", order.getBusiness());
                log.info("设置商家信息：businessId={}, businessName={}, deliveryPrice={}", 
                        order.getBusiness().getBusinessId(), 
                        order.getBusiness().getBusinessName(),
                        order.getBusiness().getDeliveryPrice());
            } else {
                log.warn("订单关联的商家信息为空：orderId={}, businessId={}", 
                        order.getOrderId(), order.getBusinessId());
                // 创建空的商家对象，避免前端出现null引用错误
                Business emptyBusiness = new Business();
                emptyBusiness.setBusinessId(order.getBusinessId());
                emptyBusiness.setBusinessName("未知商家");
                emptyBusiness.setDeliveryPrice(0.0);
                resultMap.put("business", emptyBusiness);
            }
            
            // 4. 详细记录和设置订单明细列表
            if (order.getOrderDetailList() != null && !order.getOrderDetailList().isEmpty()) {
                List<OrderDetail> detailList = order.getOrderDetailList();
                List<OrderDetail> validDetailList = new ArrayList<>();
                
                // 打印明细信息以便调试
                for (int i = 0; i < detailList.size(); i++) {
                    OrderDetail detail = detailList.get(i);
                    if (detail.getFood() != null) {
                        log.info("订单明细项[{}]: foodId={}, foodName={}, quantity={}, price={}",
                                i, detail.getFoodId(), detail.getFood().getFoodName(), 
                                detail.getQuantity(), detail.getFood().getFoodPrice());
                        validDetailList.add(detail); // 只添加有完整食品信息的明细项
                    } else {
                        log.warn("订单明细项[{}]的食品信息为空，尝试重新查询: foodId={}, quantity={}", 
                                i, detail.getFoodId(), detail.getQuantity());
                        
                        // 尝试手动查询食品信息
                        Food food = foodMapper.getFoodById(detail.getFoodId());
                        if (food != null) {
                            detail.setFood(food);
                            log.info("成功补充食品信息: foodId={}, foodName={}, foodPrice={}", 
                                    food.getFoodId(), food.getFoodName(), food.getFoodPrice());
                            validDetailList.add(detail);
                        } else {
                            log.error("无法获取食品信息: foodId={}", detail.getFoodId());
                        }
                    }
                }
                
                resultMap.put("list", validDetailList);
                log.info("设置订单明细：有效明细数量={}", validDetailList.size());
            } else {
                log.warn("订单明细列表为空：orderId={}", order.getOrderId());
                resultMap.put("list", new ArrayList<>());
                
                // 尝试手动查询订单明细
                List<OrderDetail> manualDetailList = orderMapper.listOrderDetailsByOrderId(orderId);
                if (manualDetailList != null && !manualDetailList.isEmpty()) {
                    log.info("手动查询到订单明细：数量={}", manualDetailList.size());
                    resultMap.put("list", manualDetailList);
                }
            }
            
            log.info("返回订单信息成功：orderId={}", orderId);
            return ResponseResult.success(resultMap);
        } catch (Exception e) {
            log.error("获取订单信息异常：orderId={}", orderId, e);
            return ResponseResult.error("获取订单信息异常：" + e.getMessage());
        }
    }

    // Fallback methods
    public ResponseResult<Integer> createOrderFallback(Map<String, Object> requestMap, Throwable t) {
        log.error("Circuit breaker fallback: createOrder failed", t);
        return ResponseResult.error("服务降级：创建订单失败");
    }
    
    public ResponseResult<Integer> createOrdersFallback(
            String userId, Integer businessId, Integer daId, Double orderTotal, Throwable t) {
        log.error("Circuit breaker fallback: createOrders failed", t);
        return ResponseResult.error("服务降级：创建订单失败");
    }

    public ResponseResult<Order> getOrderByIdFallback(Integer orderId, Throwable t) {
        log.error("Circuit breaker fallback: getOrderById failed", t);
        return ResponseResult.error("服务降级：获取订单失败");
    }

    public ResponseResult<List<Order>> listOrdersByUserIdFallback(String userId, Throwable t) {
        log.error("Circuit breaker fallback: listOrdersByUserId failed", t);
        return ResponseResult.error("服务降级：获取用户订单列表失败");
    }

    public ResponseResult<Integer> updateOrderStateFallback(Integer orderId, Integer orderState, Throwable t) {
        log.error("Circuit breaker fallback: updateOrderState failed", t);
        return ResponseResult.error("服务降级：更新订单状态失败");
    }

    public ResponseResult<Map<String, Object>> getOrdersByIdFallback(Integer orderId, Throwable t) {
        log.error("Circuit breaker fallback: getOrdersById failed", t);
        return ResponseResult.error("服务降级：获取订单信息失败");
    }
} 