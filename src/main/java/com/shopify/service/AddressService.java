package com.shopify.service;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.shopify.model.Address;
import com.shopify.model.Order;
import com.shopify.model.User;
import com.shopify.repository.AddressRepository;
import com.shopify.repository.OrderRepository;
import com.shopify.repository.UserRepository;


import java.util.List;

@Service
public class AddressService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private OrderRepository orderRepository;

    public void addAddressToUser(String userEmail, Address address) {
        User user = userRepository.findByEmail(userEmail).orElse(null);
        if (user != null) {
            address.setUser(user);
            if (address.isDefaultAddress()) {
                clearOtherDefaultAddresses(user);
            }
            user.getAddresses().add(address);
            userRepository.save(user);
        }
    }

    private void clearOtherDefaultAddresses(User user) {
        List<Address> addresses = user.getAddresses();
        for (Address addr : addresses) {
            if (addr.isDefaultAddress()) {
                addr.setDefaultAddress(false);
                addressRepository.save(addr);
            }
        }
    }


    public Address getAddressById(Long id) {
        return addressRepository.findById(id).orElse(null);
    }

    public void editAddress(Address address) {
        if (address.isDefaultAddress()) {
            clearOtherDefaultAddresses(address.getUser());
        }
        addressRepository.save(address);

//        updateOrderShippingAddressString(address);// for order address in order history
    }

    private void updateOrderShippingAddressString(Address address) {
        List<Order> orders = orderRepository.findByShippingAddressString(address);
        for (Order order : orders) {
            String addressString = createAddressString(address);
            order.setShippingAddressString(addressString);
            orderRepository.save(order);
        }
    }

    private String createAddressString(Address address) {
        // Creates a string representation of the address here
        // Concatenate the address fields like fullName, street, city, etc.
        return address.getFullName() + ", " + address.getStreet() + ", " + address.getCity() + ", " + address.getCountry().getName();
    }

    public void saveAddress(Address address) {
        addressRepository.save(address);
    }

    public void deleteAddressById(Long id) {
        addressRepository.deleteById(id);
    }
}
