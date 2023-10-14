package com.shopify.model;

import lombok.Data;

import javax.persistence.*;

import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Entity
@Data

public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    private BigDecimal balance;

}
