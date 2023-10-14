package com.shopify.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.shopify.model.User;
import com.shopify.model.Wallet;
import com.shopify.model.WalletTransaction;
import com.shopify.repository.UserRepository;
import com.shopify.repository.WalletRepository;
import com.shopify.repository.WalletTransactionRepository;
import com.shopify.service.UserService;
import com.shopify.service.WalletService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
public class WalletController {

    @Autowired
    private UserService userService;

    @Autowired
    private WalletService walletService;
    
    @Autowired
    UserRepository userRepository;
    
    
    @Autowired
    WalletTransactionRepository walletTransactionRepository;

    @GetMapping("/wallet")
    public String viewWallet(Model model, Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        List<WalletTransaction> walletTransaction = walletTransactionRepository.findByUser(user);
        Wallet wallet = walletService.getWalletByUser(user);
        model.addAttribute("wallet", wallet);
        model.addAttribute("walletTransaction",walletTransaction);
        return "wallet";
    }

    @PostMapping("/wallet/deposit")
    public String deposit(@RequestParam BigDecimal amount, Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        walletService.deposit(user, amount);
        return "redirect:/wallet";
    }
//
//    @PostMapping("/wallet/withdraw")
//    public String withdraw(@RequestParam BigDecimal amount, Authentication authentication, Model model) {
//        User user = userService.getUserByEmail(authentication.getName());
//        boolean success = walletService.withdraw(user, amount);
//        if (success) {
//            return "redirect:/wallet";
//        } else {
//            model.addAttribute("withdrawError", "Insufficient balance");
//            return "wallet";
//        }
//    }
}

