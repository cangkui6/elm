package com.elm.order.mapper;

import com.elm.common.entity.Order;
import com.elm.common.entity.OrderDetail;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {

    @Insert("INSERT INTO orders (userId, businessId, orderDate, orderTotal, daId, orderState) " +
            "VALUES (#{userId}, #{businessId}, #{orderDate}, #{orderTotal}, #{daId}, #{orderState})")
    @Options(useGeneratedKeys = true, keyProperty = "orderId")
    int saveOrder(Order order);

    @Insert("INSERT INTO orderdetailet (orderId, foodId, quantity) " +
            "VALUES (#{orderId}, #{foodId}, #{quantity})")
    int saveOrderDetail(OrderDetail orderDetail);

    @Select("SELECT * FROM orders WHERE orderId = #{orderId}")
    Order getOrderById(@Param("orderId") Integer orderId);
    
    @Select("SELECT * FROM orders WHERE userId = #{userId}")
    List<Order> listOrdersByUserId(@Param("userId") String userId);
    
    @Select("SELECT od.*, f.foodName, f.foodPrice, f.foodImg FROM orderdetailet od " +
            "LEFT JOIN food f ON od.foodId = f.foodId " +
            "WHERE od.orderId = #{orderId}")
    @Results({
        @Result(property = "odId", column = "odId"),
        @Result(property = "orderId", column = "orderId"),
        @Result(property = "foodId", column = "foodId"),
        @Result(property = "quantity", column = "quantity"),
        @Result(property = "food.foodId", column = "foodId"),
        @Result(property = "food.foodName", column = "foodName"),
        @Result(property = "food.foodPrice", column = "foodPrice"),
        @Result(property = "food.foodImg", column = "foodImg")
    })
    List<OrderDetail> listOrderDetailsByOrderId(@Param("orderId") Integer orderId);
    
    @Select("SELECT o.*, b.businessName, b.businessAddress, b.deliveryPrice " +
            "FROM orders o " +
            "LEFT JOIN business b ON o.businessId = b.businessId " +
            "WHERE o.orderId = #{orderId}")
    @Results({
        @Result(property = "orderId", column = "orderId"),
        @Result(property = "userId", column = "userId"),
        @Result(property = "businessId", column = "businessId"),
        @Result(property = "orderDate", column = "orderDate"),
        @Result(property = "orderTotal", column = "orderTotal"),
        @Result(property = "daId", column = "daId"),
        @Result(property = "orderState", column = "orderState"),
        @Result(property = "business.businessId", column = "businessId"),
        @Result(property = "business.businessName", column = "businessName"),
        @Result(property = "business.businessAddress", column = "businessAddress"),
        @Result(property = "business.deliveryPrice", column = "deliveryPrice")
    })
    Order getOrderWithBusinessById(@Param("orderId") Integer orderId);
    
    @Delete("DELETE FROM cart WHERE userId = #{userId} AND businessId = #{businessId}")
    int removeCart(@Param("userId") String userId, @Param("businessId") Integer businessId);
    
    @Update("UPDATE orders SET orderState = #{orderState} WHERE orderId = #{orderId}")
    int updateOrderState(@Param("orderId") Integer orderId, @Param("orderState") Integer orderState);
} 