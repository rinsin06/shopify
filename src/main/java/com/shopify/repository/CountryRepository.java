package com.shopify.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.shopify.model.Country;


public interface CountryRepository extends JpaRepository<Country, Long> {
}
