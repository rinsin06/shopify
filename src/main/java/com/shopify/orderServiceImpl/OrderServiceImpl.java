package com.shopify.orderServiceImpl;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopify.model.Address;
import com.shopify.model.Order;
import com.shopify.model.Product;
import com.shopify.repository.OrderRepository;
import com.shopify.service.OrderService;
import com.shopify.util.OrderStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private final OrderRepository orderRepository;

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }
    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public void saveOrder(Order order) {
    	
    
        orderRepository.save(order);
    }

    @Override
    public List<Order> getOrdersByShippingAddress(Address address) {
         return orderRepository.findByShippingAddressString(address);
    }

    public int getTotalOrders() {
        return (int) orderRepository.count();
    }

    @Override
    public List<Order> getRecentOrders() {
        // Create a Pageable object to fetch the top 5 recent orders sorted by orderDateTime
        Pageable pageable = PageRequest.of(0, 12, Sort.by(Sort.Direction.DESC, "orderDateTime"));

        // Fetch the recent orders
        return orderRepository.findAll(pageable).getContent();
    }

    public Page<Order> findPaginated(int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
		return this.orderRepository.findAll(pageable);
	}
    
    //method to find order with status
    
    public List<Order> findOrderByStatus(OrderStatus status)
    {
    	return orderRepository.findByOrderStatus(status);
    	
    	
    }
    
    //mthod to return the monthly earnings
    
    public List<Double> findMonthlyEarnings(int year)
    {
    	return orderRepository.calculateMonthlyEarnings(year);
    }
    
    public List<Object[]> listOfMonthsWithOrders(int year)
    {
    	return orderRepository.listMonthsWithOrders(year);
    }
    
    //method to return only orders without the status canceled and return
    
    public List<Order> findOrderNotCancelledNotReturnes(OrderStatus cancelledStatus,OrderStatus returnedStatus)
    {
    	return orderRepository.findOrdersNotCancelledOrReturned(cancelledStatus, returnedStatus);
    }
    
    public List<Double> earningsPerYear()
    {
    	return orderRepository.getTotalEarningsByYear();
    }
    
    public List<Integer> listOfYears()
    {
    	return orderRepository.findOrderYearsWithoutCanceledOrReturned();
    	
    	
    }
    
    public List<Order> findOrdersByDateRange(Date startDate, Date endDate) {
        return orderRepository.findOrdersByDateRange(startDate, endDate);
    }
    
    public List<Order> findCanceledOrdersByDateRange(Date startDate, Date endDate) {
        return orderRepository.findCanceledOrdersByDateRange(startDate, endDate);
    }

}
