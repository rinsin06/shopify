package com.shopify.model;


import lombok.Data;

import javax.persistence.*;

import com.shopify.util.OrderStatus;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "order_status_updates")
public class OrderStatusUpdate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "update_date_time")
    private LocalDateTime updateDateTime;

    @Column(name = "place_reached")
    private String placeReached;

    @Enumerated(EnumType.STRING)
    @Column(name = "original_status")
    private OrderStatus originalStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "updated_status")
    private OrderStatus updatedStatus;

}

