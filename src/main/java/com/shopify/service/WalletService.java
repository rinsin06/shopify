package com.shopify.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopify.model.User;
import com.shopify.model.Wallet;
import com.shopify.repository.WalletRepository;

import java.math.BigDecimal;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    public Wallet getWalletByUser(User user) {
        return walletRepository.findByUser(user);
    }

    public void deposit(User user, BigDecimal amount) {
        Wallet wallet = getWalletByUser(user);
        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);
    }

    public boolean withdraw(User user, BigDecimal amount) {
        Wallet wallet = getWalletByUser(user);
        BigDecimal newBalance = wallet.getBalance().subtract(amount);

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            return false; // Insufficient balance
        }

        wallet.setBalance(newBalance);
        walletRepository.save(wallet);
        return true;
    }
}
