package com.shopify.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import com.shopify.model.Product;

import java.util.List;


public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByCategory_Id(int id);
    
    @Query("SELECT p FROM Product p WHERE p.isDeleted = null")
    List<Product> findByIsDeleted();

    boolean existsByName(String name);

    @Query("SELECT p FROM Product p JOIN p.category c WHERE " +
    "LOWER(p.name) LIKE %:keyword% OR " +
   "LOWER(c.name) LIKE %:keyword%")
    public List<Product> search(String keyword); 

    @Query("SELECT p FROM Product p WHERE p.stockQuantity < 10")
    List<Product> findLowStockProducts();
    
    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.isDeleted = true WHERE p.id = ?1")
    void softDelete(Long productId);
}
