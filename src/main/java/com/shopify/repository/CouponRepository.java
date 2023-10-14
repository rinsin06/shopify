package com.shopify.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.shopify.model.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Coupon findByCode(String code);
}
