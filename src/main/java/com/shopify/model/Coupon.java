package com.shopify.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private double discountPercentage;
    private LocalDate startDate;
    private LocalDate endDate;

    @OneToMany(mappedBy = "coupon")
    private List<CouponUsage> couponUsages = new ArrayList<>();


}

