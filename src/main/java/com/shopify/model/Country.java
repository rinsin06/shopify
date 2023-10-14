package com.shopify.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "countries")
public class Country {

    @Id
    private Long id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    public Country(Country country) {
        this.code = code;
        this.name = name;
    }

    public Country(Long id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    public Country(){

    }
}
