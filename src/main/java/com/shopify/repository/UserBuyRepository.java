package com.shopify.repository;



import org.springframework.data.jpa.repository.JpaRepository;

import com.shopify.model.User;
import com.shopify.model.UserBuy;

public interface UserBuyRepository extends JpaRepository<UserBuy, Long> {
    UserBuy findByUser(User user);
}