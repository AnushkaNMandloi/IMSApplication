package com.example.order_service.service;

import com.example.order_service.dto.*;
import com.example.order_service.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface OrderService {

    // Order creation and management
    OrderResponse createOrderFromCart(Long userId, CreateOrderRequest request);
    
    OrderResponse getOrderById(Long orderId);
    
    OrderResponse getOrderByIdAndUser(Long orderId, Long userId);
    
    List<OrderSummaryResponse> getUserOrders(Long userId);
    
    Page<OrderSummaryResponse> getUserOrdersPaginated(Long userId, Pageable pageable);
    
    List<OrderSummaryResponse> getUserOrdersByStatus(Long userId, Order.OrderStatus status);
    
    Page<OrderSummaryResponse> getUserOrdersByStatusPaginated(Long userId, Order.OrderStatus status, Pageable pageable);

    // Order status management
    OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request);
    
    OrderResponse cancelOrder(Long orderId, Long userId, String reason);
    
    OrderResponse requestReturn(Long orderId, Long userId, String reason);
    
    OrderResponse confirmOrder(Long orderId, String reason);
    
    OrderResponse shipOrder(Long orderId, String trackingNumber, LocalDateTime estimatedDeliveryDate);
    
    OrderResponse deliverOrder(Long orderId, LocalDateTime actualDeliveryDate);

    // Order tracking
    OrderResponse trackOrder(String trackingNumber);
    
    List<OrderSummaryResponse> getOrdersByStatus(Order.OrderStatus status);
    
    Page<OrderSummaryResponse> getOrdersByStatusPaginated(Order.OrderStatus status, Pageable pageable);

    // Seller order management
    List<OrderSummaryResponse> getSellerOrders(Long sellerId);
    
    Page<OrderSummaryResponse> getSellerOrdersPaginated(Long sellerId, Pageable pageable);
    
    List<OrderSummaryResponse> getSellerOrdersByStatus(Long sellerId, Order.OrderStatus status);

    // Order analytics and reporting
    Map<String, Object> getOrderStatistics();
    
    Map<String, Object> getSellerOrderStatistics(Long sellerId);
    
    Map<String, Object> getRevenueStatistics(LocalDateTime startDate, LocalDateTime endDate);
    
    Map<String, Object> getSellerRevenueStatistics(Long sellerId, LocalDateTime startDate, LocalDateTime endDate);
    
    List<Map<String, Object>> getDailyOrderCounts(LocalDateTime startDate, LocalDateTime endDate);

    // Order validation and business logic
    boolean canCancelOrder(Long orderId, Long userId);
    
    boolean canReturnOrder(Long orderId, Long userId);
    
    void processAutomaticStatusUpdates();
    
    void cleanupOldOrders();

    // Payment integration
    OrderResponse updatePaymentStatus(Long orderId, Order.PaymentStatus paymentStatus, String transactionId);
    
    OrderResponse processPaymentCallback(Long orderId, Map<String, Object> paymentData);
} 