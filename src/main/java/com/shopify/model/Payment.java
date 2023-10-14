package com.shopify.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderId;

    private String amount;

    private String receipt;

    private String paymentStatus;

    private String paymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

//    @ManyToOne
//    private Order order;
}
