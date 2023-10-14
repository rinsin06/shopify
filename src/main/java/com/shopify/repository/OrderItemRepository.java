package com.shopify.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.shopify.model.OrderItem;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}
