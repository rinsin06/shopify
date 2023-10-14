package com.shopify.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Pattern;

@Entity
@Data
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Country country;

    private String fullName;

    @Pattern(regexp = "\\d{10}", message = "Mobile phone number must have exactly 10 digits")
    @Column(name = "mobile_phone_number")
    private String mobilePhoneNumber;

    private String pinCode;

    private String buildingNo;

    private String street;

    private String landmark;

    private String city;

    private String state;

    @Column(name = "is_default")
    private boolean defaultAddress;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Address(Address address) {
        this.fullName = address.getFullName();
        this.mobilePhoneNumber = address.getMobilePhoneNumber();
        this.pinCode = address.getPinCode();
        this.buildingNo = address.getBuildingNo();
        this.street = address.getStreet();
        this.landmark = address.getLandmark();
        this.city = address.getCity();
        this.state = address.getState();
        this.defaultAddress = address.isDefaultAddress();
    }

    public Address(){

    }
}
