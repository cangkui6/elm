package com.elm.order.mapper;

import com.elm.common.entity.Order;
import com.elm.common.entity.OrderDetail;
import org.apache.ibatis.annotations.*;

import java.util.List;

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
    
    @Select("SELECT * FROM orderdetailet WHERE orderId = #{orderId}")
    List<OrderDetail> listOrderDetailsByOrderId(@Param("orderId") Integer orderId);
    
    @Delete("DELETE FROM cart WHERE userId = #{userId} AND businessId = #{businessId}")
    int removeCart(@Param("userId") String userId, @Param("businessId") Integer businessId);
    
    @Update("UPDATE orders SET orderState = #{orderState} WHERE orderId = #{orderId}")
    int updateOrderState(@Param("orderId") Integer orderId, @Param("orderState") Integer orderState);
} 