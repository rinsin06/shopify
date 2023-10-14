package com.shopify.repository;



import org.springframework.data.jpa.repository.JpaRepository;

import com.shopify.model.Address;
import com.shopify.model.User;

import java.util.List;


public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findAddressesByUser(User user);
}
