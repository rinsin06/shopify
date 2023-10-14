package com.shopify.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.shopify.model.ProductImage;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
}
