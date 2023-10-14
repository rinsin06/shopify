package com.shopify.model;

import lombok.Data;

import javax.persistence.*;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Entity
@Data
@Service
public class WalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private BigDecimal transaction;

}