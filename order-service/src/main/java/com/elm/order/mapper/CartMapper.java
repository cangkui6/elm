package com.elm.order.mapper;

import com.elm.common.entity.Cart;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CartMapper {

    @Select("SELECT * FROM cart WHERE userId = #{userId} AND businessId = #{businessId}")
    @Results({
        @Result(column = "cartId", property = "cartId"),
        @Result(column = "foodId", property = "foodId"),
        @Result(column = "businessId", property = "businessId"),
        @Result(column = "userId", property = "userId"),
        @Result(column = "quantity", property = "quantity"),
        @Result(column = "foodId", property = "food", 
            one = @One(select = "com.elm.order.mapper.FoodMapper.getFoodById")),
        @Result(column = "businessId", property = "business", 
            one = @One(select = "com.elm.order.mapper.BusinessMapper.getBusinessById"))
    })
    List<Cart> listCart(@Param("userId") Integer userId, @Param("businessId") Integer businessId);

    @Select("SELECT * FROM cart WHERE userId = #{userId}")
    @Results({
        @Result(column = "cartId", property = "cartId"),
        @Result(column = "foodId", property = "foodId"),
        @Result(column = "businessId", property = "businessId"),
        @Result(column = "userId", property = "userId"),
        @Result(column = "quantity", property = "quantity"),
        @Result(column = "foodId", property = "food", 
            one = @One(select = "com.elm.order.mapper.FoodMapper.getFoodById")),
        @Result(column = "businessId", property = "business", 
            one = @One(select = "com.elm.order.mapper.BusinessMapper.getBusinessById"))
    })
    List<Cart> listCartByUserId(@Param("userId") Integer userId);

    @Insert("INSERT INTO cart (foodId, businessId, userId) VALUES (#{foodId}, #{businessId}, #{userId})")
    int saveCart(Cart cart);

    @Update("UPDATE cart SET quantity = #{quantity} WHERE userId = #{userId} AND businessId = #{businessId} AND foodId = #{foodId}")
    int updateCart(Cart cart);

    @Delete("DELETE FROM cart WHERE userId = #{userId} AND businessId = #{businessId} AND foodId = #{foodId}")
    int removeCart(@Param("userId") Integer userId, @Param("businessId") Integer businessId, @Param("foodId") Integer foodId);

    @Delete("DELETE FROM cart WHERE userId = #{userId} AND businessId = #{businessId}")
    int removeCartByUserIdAndBusinessId(@Param("userId") Integer userId, @Param("businessId") Integer businessId);
} 