package com.shopify.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.shopify.model.Role;


public interface RoleRepository extends JpaRepository<Role, Integer> {
	
	Role findByName(String role);
	
		
	
}
