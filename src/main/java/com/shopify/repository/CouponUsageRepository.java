package com.shopify.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shopify.model.Coupon;
import com.shopify.model.CouponUsage;
import com.shopify.model.User;

@Repository
public interface CouponUsageRepository extends JpaRepository<CouponUsage, Long> {
	
    boolean existsByUserAndCoupon(User user, Coupon coupon);
    
    void deleteByUserAndCoupon(User user, Coupon coupon);
}
