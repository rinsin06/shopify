package com.shopify.service;




import java.util.List;
import java.util.Optional;

import com.shopify.model.Address;
import com.shopify.model.Order;

public interface OrderService {
    List<Order> getAllOrders();
    Optional<Order> getOrderById(Long orderId);

    List<Order> getOrdersByShippingAddress(Address address);
    void saveOrder(Order order);

    int getTotalOrders();

    List<Order> getRecentOrders();
    
    

}
