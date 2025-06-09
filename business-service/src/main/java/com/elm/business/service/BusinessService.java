package com.elm.business.service;

import com.elm.common.entity.Business;

import java.util.List;

public interface BusinessService {

    List<Business> listBusinessByOrderTypeId(Integer orderTypeId);

    Business getBusinessById(Integer businessId);

    List<Business> listAllBusinesses();
} 