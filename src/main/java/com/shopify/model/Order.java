package com.shopify.model;


import lombok.Data;

import javax.persistence.*;

import com.shopify.util.OrderStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "orders")
public class Order  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "order_date_time")
    private LocalDateTime orderDateTime;

    public String getFormattedOrderDate() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return orderDateTime.format(dateFormatter);
    }

    public String getFormattedOrderTime() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return orderDateTime.format(timeFormatter);
    }

    @Column(name = "total_amount")
    private double totalAmount;
    
    @Column(name = "discount_amount")
    private Double discountAmount;

    @Column(name = "payment_method")
    private String paymentMethod;


    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Column(name = "shipping_address_string")
    private String shippingAddressString;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private OrderStatus status;



    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderStatusUpdate> statusUpdates = new ArrayList<>();

    public void addStatusUpdate(OrderStatusUpdate statusUpdate) {
        statusUpdates.add(statusUpdate);
        statusUpdate.setOrder(this);
    }
    public void updateOrderStatusAndPlace(OrderStatus newStatus, String placeReached) {
        OrderStatusUpdate statusUpdate = new OrderStatusUpdate();
        statusUpdate.setPlaceReached(placeReached);
        statusUpdate.setUpdateDateTime(LocalDateTime.now());
        statusUpdate.setOriginalStatus(this.getStatus()); // Store the original status
        statusUpdate.setUpdatedStatus(newStatus);

        this.addStatusUpdate(statusUpdate);
        this.setStatus(newStatus);
    }

//    @ManyToOne
//    private Coupon coupon;

}
