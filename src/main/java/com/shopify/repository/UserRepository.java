package com.shopify.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import com.shopify.model.Product;
import com.shopify.model.User;
@EnableJpaRepositories

public interface UserRepository extends JpaRepository<User, Long> {
	
	List<User> findAll();
	
	Optional<User> findById(Long userId);
	
	Optional<User> findByEmail(String email);
	
	@Query("SELECT u FROM User u WHERE " +
		       "LOWER(u.firstName) LIKE %:keyword% OR " +
		       "LOWER(u.phoneNumber) LIKE %:keyword% OR " +
		       "LOWER(u.email) LIKE %:keyword%")
	    public List<User> search( String keyword);
	
	
//	
//	User findByUsername(String name);


}
