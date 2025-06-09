package com.elm.business.service.impl;

import com.elm.business.mapper.FoodCategoryMapper;
import com.elm.business.service.FoodCategoryService;
import com.elm.common.entity.FoodCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FoodCategoryServiceImpl implements FoodCategoryService {

    @Autowired
    private FoodCategoryMapper foodCategoryMapper;

    @Override
    public List<FoodCategory> listFoodCategories() {
        return foodCategoryMapper.listFoodCategories();
    }
} 