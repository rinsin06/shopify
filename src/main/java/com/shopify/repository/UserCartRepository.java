package com.shopify.repository;



import org.springframework.data.jpa.repository.JpaRepository;

import com.shopify.model.User;
import com.shopify.model.UserCart;


public interface UserCartRepository extends JpaRepository<UserCart, Long> {
    UserCart findByUser(User user);
}
