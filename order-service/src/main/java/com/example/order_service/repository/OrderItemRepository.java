package com.example.order_service.repository;

import com.example.order_service.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // Find order items by order
    List<OrderItem> findByOrderId(Long orderId);

    // Find order items by item
    List<OrderItem> findByItemId(Long itemId);

    // Find order items by seller
    List<OrderItem> findBySellerIdOrderByIdDesc(Long sellerId);

    // Find order items by seller and date range
    @Query("SELECT oi FROM OrderItem oi WHERE oi.sellerId = :sellerId AND oi.order.createdAt BETWEEN :startDate AND :endDate")
    List<OrderItem> findBySellerAndDateRange(@Param("sellerId") Long sellerId, 
                                           @Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate);

    // Get seller's total sales quantity
    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.sellerId = :sellerId AND oi.order.status = 'DELIVERED'")
    Long getTotalQuantitySoldBySeller(@Param("sellerId") Long sellerId);

    // Get item's total sales quantity
    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.itemId = :itemId AND oi.order.status = 'DELIVERED'")
    Long getTotalQuantitySoldByItem(@Param("itemId") Long itemId);

    // Get top selling items
    @Query("SELECT oi.itemId, oi.itemName, SUM(oi.quantity) as totalSold FROM OrderItem oi WHERE oi.order.status = 'DELIVERED' GROUP BY oi.itemId, oi.itemName ORDER BY totalSold DESC")
    List<Object[]> getTopSellingItems();

    // Get seller's top selling items
    @Query("SELECT oi.itemId, oi.itemName, SUM(oi.quantity) as totalSold FROM OrderItem oi WHERE oi.sellerId = :sellerId AND oi.order.status = 'DELIVERED' GROUP BY oi.itemId, oi.itemName ORDER BY totalSold DESC")
    List<Object[]> getTopSellingItemsBySeller(@Param("sellerId") Long sellerId);
} 