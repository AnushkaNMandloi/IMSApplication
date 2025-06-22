package com.example.order_service.repository;

import com.example.order_service.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Find orders by user
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    Page<Order> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // Find orders by status
    List<Order> findByStatusOrderByCreatedAtDesc(Order.OrderStatus status);
    
    Page<Order> findByStatusOrderByCreatedAtDesc(Order.OrderStatus status, Pageable pageable);

    // Find orders by user and status
    List<Order> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, Order.OrderStatus status);
    
    Page<Order> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, Order.OrderStatus status, Pageable pageable);

    // Find orders by date range
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate ORDER BY o.createdAt DESC")
    List<Order> findOrdersByDateRange(@Param("startDate") LocalDateTime startDate, 
                                     @Param("endDate") LocalDateTime endDate);

    // Find orders by user and date range
    @Query("SELECT o FROM Order o WHERE o.userId = :userId AND o.createdAt BETWEEN :startDate AND :endDate ORDER BY o.createdAt DESC")
    List<Order> findOrdersByUserAndDateRange(@Param("userId") Long userId,
                                           @Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate);

    // Find orders by payment status
    List<Order> findByPaymentStatusOrderByCreatedAtDesc(Order.PaymentStatus paymentStatus);

    // Find orders by tracking number
    Optional<Order> findByTrackingNumber(String trackingNumber);

    // Find pending orders older than specified time
    @Query("SELECT o FROM Order o WHERE o.status = 'PENDING' AND o.createdAt < :cutoffTime")
    List<Order> findPendingOrdersOlderThan(@Param("cutoffTime") LocalDateTime cutoffTime);

    // Count orders by status
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    Long countByStatus(@Param("status") Order.OrderStatus status);

    // Count orders by user
    Long countByUserId(Long userId);

    // Find orders that can be cancelled
    @Query("SELECT o FROM Order o WHERE o.status IN ('PENDING', 'CONFIRMED') AND o.userId = :userId")
    List<Order> findCancellableOrdersByUser(@Param("userId") Long userId);

    // Find orders that can be returned
    @Query("SELECT o FROM Order o WHERE o.status = 'DELIVERED' AND o.actualDeliveryDate > :cutoffDate AND o.userId = :userId")
    List<Order> findReturnableOrdersByUser(@Param("userId") Long userId, @Param("cutoffDate") LocalDateTime cutoffDate);

    // Find orders by seller (through order items)
    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderItems oi WHERE oi.sellerId = :sellerId ORDER BY o.createdAt DESC")
    List<Order> findOrdersBySeller(@Param("sellerId") Long sellerId);

    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderItems oi WHERE oi.sellerId = :sellerId ORDER BY o.createdAt DESC")
    Page<Order> findOrdersBySeller(@Param("sellerId") Long sellerId, Pageable pageable);

    // Find orders by seller and status
    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderItems oi WHERE oi.sellerId = :sellerId AND o.status = :status ORDER BY o.createdAt DESC")
    List<Order> findOrdersBySellerAndStatus(@Param("sellerId") Long sellerId, @Param("status") Order.OrderStatus status);

    // Revenue analytics queries
    @Query("SELECT SUM(o.finalAmount) FROM Order o WHERE o.status = 'DELIVERED' AND o.createdAt BETWEEN :startDate AND :endDate")
    Double getTotalRevenueByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(o.finalAmount) FROM Order o JOIN o.orderItems oi WHERE oi.sellerId = :sellerId AND o.status = 'DELIVERED' AND o.createdAt BETWEEN :startDate AND :endDate")
    Double getSellerRevenueByDateRange(@Param("sellerId") Long sellerId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Order statistics
    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> getOrderStatusStatistics();

    @Query("SELECT DATE(o.createdAt), COUNT(o) FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate GROUP BY DATE(o.createdAt)")
    List<Object[]> getDailyOrderCounts(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
} 