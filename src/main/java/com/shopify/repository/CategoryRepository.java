package com.shopify.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.shopify.model.Category;


public interface CategoryRepository extends JpaRepository<Category, Integer> {
    boolean existsByName(String name);
}
