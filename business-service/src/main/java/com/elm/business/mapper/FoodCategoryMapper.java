package com.elm.business.mapper;

import com.elm.common.entity.FoodCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FoodCategoryMapper {

    @Select("SELECT * FROM food_category")
    List<FoodCategory> listFoodCategories();
} 