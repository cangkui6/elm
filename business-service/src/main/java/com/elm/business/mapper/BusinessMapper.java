package com.elm.business.mapper;

import com.elm.common.entity.Business;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BusinessMapper {

    @Select("SELECT * FROM business WHERE orderTypeId=#{orderTypeId}")
    List<Business> listBusinessByOrderTypeId(@Param("orderTypeId") Integer orderTypeId);

    @Select("SELECT * FROM business WHERE businessId=#{businessId}")
    Business getBusinessById(@Param("businessId") Integer businessId);

    @Select("SELECT * FROM business")
    List<Business> listAllBusinesses();
} 