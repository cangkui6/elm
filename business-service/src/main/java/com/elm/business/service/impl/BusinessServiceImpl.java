package com.elm.business.service.impl;

import com.elm.business.mapper.BusinessMapper;
import com.elm.business.service.BusinessService;
import com.elm.common.entity.Business;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BusinessServiceImpl implements BusinessService {

    @Autowired
    private BusinessMapper businessMapper;

    @Override
    public List<Business> listBusinessByOrderTypeId(Integer orderTypeId) {
        return businessMapper.listBusinessByOrderTypeId(orderTypeId);
    }

    @Override
    public Business getBusinessById(Integer businessId) {
        return businessMapper.getBusinessById(businessId);
    }

    @Override
    public List<Business> listAllBusinesses() {
        return businessMapper.listAllBusinesses();
    }
} 