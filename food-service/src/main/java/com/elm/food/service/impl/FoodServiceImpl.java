package com.elm.food.service.impl;

import com.elm.food.mapper.FoodMapper;
import com.elm.food.service.FoodService;
import com.elm.common.entity.Food;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FoodServiceImpl implements FoodService {

    @Autowired
    private FoodMapper foodMapper;

    @Override
    public List<Food> listFoodByBusinessId(Integer businessId) {
        return foodMapper.listFoodByBusinessId(businessId);
    }

    @Override
    public Food getFoodById(Integer foodId) {
        return foodMapper.getFoodById(foodId);
    }
} 