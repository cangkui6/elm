package com.elm.user.mapper;

import com.elm.common.entity.DeliveryAddress;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DeliveryAddressMapper {

    @Select("SELECT * FROM delivery_address WHERE daId = #{daId}")
    DeliveryAddress getDeliveryAddressById(@Param("daId") Integer daId);

    @Select("SELECT * FROM delivery_address WHERE userId = #{userId}")
    List<DeliveryAddress> listDeliveryAddressByUserId(@Param("userId") Integer userId);

    @Insert("INSERT INTO delivery_address (userId, contactName, contactSex, contactTel, address) " +
            "VALUES (#{userId}, #{contactName}, #{contactSex}, #{contactTel}, #{address})")
    @Options(useGeneratedKeys = true, keyProperty = "daId")
    int saveDeliveryAddress(DeliveryAddress deliveryAddress);

    @Update("UPDATE delivery_address SET contactName = #{contactName}, contactSex = #{contactSex}, " +
            "contactTel = #{contactTel}, address = #{address} WHERE daId = #{daId}")
    int updateDeliveryAddress(DeliveryAddress deliveryAddress);

    @Delete("DELETE FROM delivery_address WHERE daId = #{daId}")
    int removeDeliveryAddress(@Param("daId") Integer daId);
} 