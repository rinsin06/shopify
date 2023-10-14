package com.shopify.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopify.model.Coupon;
import com.shopify.model.User;
import com.shopify.repository.CouponUsageRepository;
@Service
public class CouponUsageService {
	
	@Autowired
	
	   CouponUsageRepository couponUsageRepository;

	 @Transactional
	    public void deleteCouponUsage(User user, Coupon coupon) {
	        couponUsageRepository.deleteByUserAndCoupon(user, coupon);
	    }

}
