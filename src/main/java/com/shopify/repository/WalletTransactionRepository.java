package com.shopify.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopify.model.User;
import com.shopify.model.WalletTransaction;

public interface WalletTransactionRepository  extends JpaRepository<WalletTransaction, Long> {
	
//	List<WalletTransaction> walletTransactions(User user);
	
	List<WalletTransaction> findByUser(User user);

}
