package com.elm.consumer.feign;

import com.elm.common.entity.DeliveryAddress;
import com.elm.common.result.ResponseResult;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Primary
public class AddressClientFallback implements AddressClient {

    @Override
    public ResponseResult<List<DeliveryAddress>> listDeliveryAddressByUserId(String userId) {
        return ResponseResult.error("地址服务不可用：获取地址列表失败");
    }

    @Override
    public ResponseResult<DeliveryAddress> getDeliveryAddressById(Integer daId) {
        return ResponseResult.error("地址服务不可用：获取地址详情失败");
    }

    @Override
    public ResponseResult<Integer> saveDeliveryAddress(String userId, String contactName, String contactSex, 
                                             String contactTel, String address) {
        return ResponseResult.error("地址服务不可用：保存地址失败");
    }

    @Override
    public ResponseResult<Integer> updateDeliveryAddress(Integer daId, String userId, String contactName, 
                                              String contactSex, String contactTel, String address) {
        return ResponseResult.error("地址服务不可用：更新地址失败");
    }

    @Override
    public ResponseResult<Integer> removeDeliveryAddress(Integer daId) {
        return ResponseResult.error("地址服务不可用：删除地址失败");
    }
} 