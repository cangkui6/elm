package com.elm.cart.mapper;

import com.elm.common.entity.Food;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FoodMapper {

    @Select("SELECT * FROM food WHERE businessId = #{businessId}")
    List<Food> listFoodByBusinessId(@Param("businessId") Integer businessId);
    
    @Select("SELECT * FROM food WHERE foodId = #{foodId}")
    Food getFoodById(@Param("foodId") Integer foodId);
} 