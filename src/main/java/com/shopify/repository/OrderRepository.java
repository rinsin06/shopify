package com.shopify.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shopify.model.Address;
import com.shopify.model.Order;
import com.shopify.model.User;
import com.shopify.util.OrderStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
    List<Order> findByShippingAddressString(Address addressString);
    
    
    
    @Query("SELECT o FROM Order o WHERE o.status = :status")
    List<Order> findByOrderStatus( @Param("status") OrderStatus status);
    
    
    @Query("SELECT o FROM Order o WHERE o.status <> :cancelledStatus AND o.status <> :returnedStatus")
    List<Order> findOrdersNotCancelledOrReturned(
        @Param("cancelledStatus") OrderStatus cancelledStatus,
        @Param("returnedStatus") OrderStatus returnedStatus
    );
    
    @Query("SELECT o FROM Order o WHERE MONTH(o.orderDateTime) = :month")
    List<Order> findOrdersByMonth(int month);
    
    @Query("SELECT SUM(o.totalAmount) " +
            "FROM Order o " +
            "WHERE YEAR(o.orderDateTime) = :year " +
            "AND o.status NOT IN ('RETURN', 'CANCELED') " +
            "GROUP BY MONTH(o.orderDateTime) " +
            "ORDER BY MONTH(o.orderDateTime)")
     List<Double> calculateMonthlyEarnings(int year);
    
    @Query("SELECT DISTINCT  MONTHNAME(o.orderDateTime), MONTH(o.orderDateTime) " +
            "FROM Order o " +
            "WHERE YEAR(o.orderDateTime) = :year " +
            "ORDER BY MONTH(o.orderDateTime)")
     List<Object[]> listMonthsWithOrders(int year);
     
     @Query("SELECT SUM(o.totalAmount) " +
             "FROM Order o " +
             "WHERE o.status NOT IN ('CANCELED', 'RETURN') " +
             "GROUP BY YEAR(o.orderDateTime)")
      List<Double> getTotalEarningsByYear();
     
     @Query("SELECT DISTINCT YEAR(o.orderDateTime) FROM Order o WHERE o.status NOT IN ('CANCELED', 'RETURN')")
     List<Integer> findOrderYearsWithoutCanceledOrReturned();

     @Query("SELECT o FROM Order o WHERE DATE(o.orderDateTime) BETWEEN :startDate AND :endDate AND o.status NOT IN ('CANCELED', 'RETURN')")
     List<Order> findOrdersByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
 
     @Query("SELECT o FROM Order o WHERE DATE(o.orderDateTime) BETWEEN :startDate AND :endDate AND (o.status = 'CANCELED')")
     List<Order> findCanceledOrdersByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
