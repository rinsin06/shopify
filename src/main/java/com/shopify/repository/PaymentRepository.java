package com.shopify.repository;



import org.springframework.data.jpa.repository.JpaRepository;

import com.shopify.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    public Payment findByOrderId(String orderId);
}
