package com.elm.address.mapper;

import com.elm.common.entity.DeliveryAddress;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DeliveryAddressMapper {

    @Select("SELECT * FROM deliveryaddress WHERE daId = #{daId}")
    DeliveryAddress getDeliveryAddressById(@Param("daId") Integer daId);

    @Select("SELECT * FROM deliveryaddress WHERE userId = #{userId}")
    List<DeliveryAddress> listDeliveryAddressByUserId(@Param("userId") String userId);

    @Insert("INSERT INTO deliveryaddress (userId, contactName, contactSex, contactTel, address) " +
            "VALUES (#{userId}, #{contactName}, #{contactSex}, #{contactTel}, #{address})")
    @Options(useGeneratedKeys = true, keyProperty = "daId")
    int saveDeliveryAddress(DeliveryAddress deliveryAddress);

    @Update("UPDATE deliveryaddress SET contactName = #{contactName}, contactSex = #{contactSex}, " +
            "contactTel = #{contactTel}, address = #{address} WHERE daId = #{daId}")
    int updateDeliveryAddress(DeliveryAddress deliveryAddress);

    @Delete("DELETE FROM deliveryaddress WHERE daId = #{daId}")
    int removeDeliveryAddress(@Param("daId") Integer daId);
} 