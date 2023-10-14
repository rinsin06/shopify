package com.shopify.model;

import lombok.Data;
import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class UserBuy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToMany
    private List<Product> products = new ArrayList<>();

    private double totalAmount;
    public double getBuyAmount() {
        double totalAmount = 0.0;
        for (Product product : products) {
            totalAmount += product.getPrice();
        }
        return totalAmount;
    }

    public void setBuyAmount( double totalAmount){
        this.totalAmount = totalAmount;

    }

    public UserBuy(User user, List<Product> products) {
        this.user = user;
        this.products = products;
    }

    public UserBuy() {
    }
}

