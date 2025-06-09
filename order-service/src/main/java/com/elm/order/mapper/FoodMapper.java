package com.elm.order.mapper;

import com.elm.common.entity.Food;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FoodMapper {

    @Select("SELECT * FROM food WHERE foodId=#{foodId}")
    Food getFoodById(@Param("foodId") Integer foodId);
} 