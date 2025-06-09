package com.elm.order.mapper;

import com.elm.common.entity.Business;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface BusinessMapper {

    @Select("SELECT * FROM business WHERE businessId=#{businessId}")
    Business getBusinessById(@Param("businessId") Integer businessId);
} 