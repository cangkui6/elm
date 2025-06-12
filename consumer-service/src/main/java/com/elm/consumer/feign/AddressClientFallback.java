package com.elm.consumer.feign;

import com.elm.common.entity.DeliveryAddress;
import com.elm.common.result.ResponseResult;

import java.util.List;

public class AddressClientFallback implements AddressClient {

    @Override
    public ResponseResult<List<DeliveryAddress>> listDeliveryAddressByUserId(String userId) {
        return ResponseResult.error("地址服務不可用：獲取地址列表失敗");
    }

    @Override
    public ResponseResult<DeliveryAddress> getDeliveryAddressById(Integer daId) {
        return ResponseResult.error("地址服務不可用：獲取地址詳情失敗");
    }

    @Override
    public ResponseResult<Integer> saveDeliveryAddress(String userId, String contactName, String contactSex, String contactTel, String address) {
        return ResponseResult.error("地址服務不可用：保存地址失敗");
    }

    @Override
    public ResponseResult<Integer> updateDeliveryAddress(Integer daId, String userId, String contactName, String contactSex, String contactTel, String address) {
        return ResponseResult.error("地址服務不可用：更新地址失敗");
    }

    @Override
    public ResponseResult<Integer> removeDeliveryAddress(Integer daId) {
        return ResponseResult.error("地址服務不可用：刪除地址失敗");
    }
} 