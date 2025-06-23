package com.example.order_service.service.impl;

import com.example.order_service.dto.*;
import com.example.order_service.feign.CartServiceClient;
import com.example.order_service.feign.UserServiceClient;
import com.example.order_service.model.Order;
import com.example.order_service.model.OrderItem;
import com.example.order_service.repository.OrderRepository;
import com.example.order_service.repository.OrderItemRepository;
import com.example.order_service.service.OrderService;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartServiceClient cartServiceClient;

    @Autowired
    private UserServiceClient userServiceClient;

    @Override
    public OrderResponse createOrderFromCart(Long userId, CreateOrderRequest request) {
        logger.info("Creating order from cart {} for user {}", request.getCartId(), userId);

        try {
            // Get cart details
            CartServiceClient.CartDto cart = cartServiceClient.getCartById(request.getCartId()).getBody();
            if (cart == null || cart.getItems().isEmpty()) {
                throw new RuntimeException("Cart is empty or not found");
            }

            // Verify cart belongs to user
            if (!userId.equals(cart.getUserId())) {
                throw new RuntimeException("Cart does not belong to the user");
            }

            // Get user details
            UserServiceClient.UserDto user = getUserDetails(userId);

            // Create order
            Order order = new Order();
            order.setUserId(userId);
            order.setCustomerName(user.getFullName());
            order.setCustomerEmail(user.getEmail());
            order.setCustomerPhone(user.getPhoneNumber());
            order.setShippingAddress(request.getShippingAddress());
            order.setBillingAddress(request.getBillingAddress() != null ? 
                    request.getBillingAddress() : request.getShippingAddress());
            order.setPaymentMethod(request.getPaymentMethod());
            order.setNotes(request.getNotes());
            order.setShippingCost(request.getShippingCost());
            order.setTaxAmount(request.getTaxAmount());
            order.setDiscountAmount(request.getDiscountAmount());

            // Convert cart items to order items
            final Order finalOrder = order; // Make order effectively final for lambda
            List<OrderItem> orderItems = cart.getItems().stream()
                    .map(cartItem -> {
                        OrderItem orderItem = new OrderItem();
                        orderItem.setItemId(cartItem.getItemId());
                        orderItem.setItemName(cartItem.getItemName());
                        orderItem.setItemDescription(cartItem.getItemDescription());
                        orderItem.setPrice(cartItem.getPrice());
                        orderItem.setQuantity(cartItem.getQuantity());
                        orderItem.setSellerId(cartItem.getSellerId());
                        orderItem.setSellerName(cartItem.getSellerName());
                        orderItem.setImageUrl(cartItem.getImageUrl());
                        orderItem.setCategory(cartItem.getCategory());
                        orderItem.setProductAttributes(cartItem.getProductAttributes());
                        orderItem.calculateSubtotal();
                        return orderItem;
                    })
                    .collect(Collectors.toList());

            // Add all order items to the order
            for (OrderItem orderItem : orderItems) {
                order.addOrderItem(orderItem);
            }

            // Calculate totals
            order.calculateTotalAmount();
            order.calculateFinalAmount();

            // Set estimated delivery date (7 days from now)
            order.setEstimatedDeliveryDate(LocalDateTime.now().plusDays(7));

            // Save order
            order = orderRepository.save(order);

            // Mark cart as converted
            try {
                cartServiceClient.markCartAsConverted(request.getCartId());
            } catch (Exception e) {
                logger.warn("Failed to mark cart as converted: {}", e.getMessage());
            }

            logger.info("Order {} created successfully for user {}", order.getId(), userId);
            return new OrderResponse(order);

        } catch (FeignException e) {
            logger.error("Error communicating with external service: {}", e.getMessage());
            throw new RuntimeException("Failed to create order due to service communication error");
        } catch (Exception e) {
            logger.error("Error creating order: {}", e.getMessage());
            throw new RuntimeException("Failed to create order: " + e.getMessage());
        }
    }

    @Override
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        return new OrderResponse(order);
    }

    @Override
    public OrderResponse getOrderByIdAndUser(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("Order does not belong to the user");
        }
        
        return new OrderResponse(order);
    }

    @Override
    public List<OrderSummaryResponse> getUserOrders(Long userId) {
        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return orders.stream()
                .map(OrderSummaryResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public Page<OrderSummaryResponse> getUserOrdersPaginated(Long userId, Pageable pageable) {
        Page<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return orders.map(OrderSummaryResponse::new);
    }

    @Override
    public List<OrderSummaryResponse> getUserOrdersByStatus(Long userId, Order.OrderStatus status) {
        List<Order> orders = orderRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, status);
        return orders.stream()
                .map(OrderSummaryResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public Page<OrderSummaryResponse> getUserOrdersByStatusPaginated(Long userId, Order.OrderStatus status, Pageable pageable) {
        Page<Order> orders = orderRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, status, pageable);
        return orders.map(OrderSummaryResponse::new);
    }

    @Override
    public OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        order.updateStatus(request.getStatus(), request.getReason());
        
        if (request.getTrackingNumber() != null) {
            order.setTrackingNumber(request.getTrackingNumber());
        }

        order = orderRepository.save(order);
        logger.info("Order {} status updated to {}", orderId, request.getStatus());
        
        return new OrderResponse(order);
    }

    @Override
    public OrderResponse cancelOrder(Long orderId, Long userId, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("Order does not belong to the user");
        }

        if (!order.canBeCancelled()) {
            throw new RuntimeException("Order cannot be cancelled in current status: " + order.getStatus());
        }

        order.updateStatus(Order.OrderStatus.CANCELLED, reason != null ? reason : "Cancelled by user");
        order = orderRepository.save(order);

        logger.info("Order {} cancelled by user {}", orderId, userId);
        return new OrderResponse(order);
    }

    @Override
    public OrderResponse requestReturn(Long orderId, Long userId, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("Order does not belong to the user");
        }

        if (!order.canBeReturned()) {
            throw new RuntimeException("Order cannot be returned");
        }

        order.updateStatus(Order.OrderStatus.RETURNED, reason != null ? reason : "Return requested by user");
        order = orderRepository.save(order);

        logger.info("Return requested for order {} by user {}", orderId, userId);
        return new OrderResponse(order);
    }

    @Override
    public OrderResponse confirmOrder(Long orderId, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new RuntimeException("Order cannot be confirmed in current status: " + order.getStatus());
        }

        order.updateStatus(Order.OrderStatus.CONFIRMED, reason != null ? reason : "Order confirmed");
        order = orderRepository.save(order);

        logger.info("Order {} confirmed", orderId);
        return new OrderResponse(order);
    }

    @Override
    public OrderResponse shipOrder(Long orderId, String trackingNumber, LocalDateTime estimatedDeliveryDate) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        if (order.getStatus() != Order.OrderStatus.CONFIRMED && order.getStatus() != Order.OrderStatus.PROCESSING) {
            throw new RuntimeException("Order cannot be shipped in current status: " + order.getStatus());
        }

        order.updateStatus(Order.OrderStatus.SHIPPED, "Order shipped");
        order.setTrackingNumber(trackingNumber);
        if (estimatedDeliveryDate != null) {
            order.setEstimatedDeliveryDate(estimatedDeliveryDate);
        }
        order = orderRepository.save(order);

        logger.info("Order {} shipped with tracking number {}", orderId, trackingNumber);
        return new OrderResponse(order);
    }

    @Override
    public OrderResponse deliverOrder(Long orderId, LocalDateTime actualDeliveryDate) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        if (order.getStatus() != Order.OrderStatus.SHIPPED && order.getStatus() != Order.OrderStatus.OUT_FOR_DELIVERY) {
            throw new RuntimeException("Order cannot be delivered in current status: " + order.getStatus());
        }

        order.updateStatus(Order.OrderStatus.DELIVERED, "Order delivered");
        order.setActualDeliveryDate(actualDeliveryDate != null ? actualDeliveryDate : LocalDateTime.now());
        order = orderRepository.save(order);

        logger.info("Order {} delivered", orderId);
        return new OrderResponse(order);
    }

    @Override
    public OrderResponse trackOrder(String trackingNumber) {
        Order order = orderRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new RuntimeException("Order not found with tracking number: " + trackingNumber));
        return new OrderResponse(order);
    }

    @Override
    public List<OrderSummaryResponse> getOrdersByStatus(Order.OrderStatus status) {
        List<Order> orders = orderRepository.findByStatusOrderByCreatedAtDesc(status);
        return orders.stream()
                .map(OrderSummaryResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public Page<OrderSummaryResponse> getOrdersByStatusPaginated(Order.OrderStatus status, Pageable pageable) {
        Page<Order> orders = orderRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
        return orders.map(OrderSummaryResponse::new);
    }

    @Override
    public List<OrderSummaryResponse> getSellerOrders(Long sellerId) {
        List<Order> orders = orderRepository.findOrdersBySeller(sellerId);
        return orders.stream()
                .map(OrderSummaryResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public Page<OrderSummaryResponse> getSellerOrdersPaginated(Long sellerId, Pageable pageable) {
        Page<Order> orders = orderRepository.findOrdersBySeller(sellerId, pageable);
        return orders.map(OrderSummaryResponse::new);
    }

    @Override
    public List<OrderSummaryResponse> getSellerOrdersByStatus(Long sellerId, Order.OrderStatus status) {
        List<Order> orders = orderRepository.findOrdersBySellerAndStatus(sellerId, status);
        return orders.stream()
                .map(OrderSummaryResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getOrderStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        List<Object[]> statusStats = orderRepository.getOrderStatusStatistics();
        Map<String, Long> statusCounts = new HashMap<>();
        for (Object[] stat : statusStats) {
            statusCounts.put(stat[0].toString(), (Long) stat[1]);
        }
        
        stats.put("statusCounts", statusCounts);
        stats.put("totalOrders", orderRepository.count());
        
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        Double revenue = orderRepository.getTotalRevenueByDateRange(thirtyDaysAgo, LocalDateTime.now());
        stats.put("revenueLastMonth", revenue != null ? revenue : 0.0);
        
        return stats;
    }

    @Override
    public Map<String, Object> getSellerOrderStatistics(Long sellerId) {
        Map<String, Object> stats = new HashMap<>();
        
        List<Order> sellerOrders = orderRepository.findOrdersBySeller(sellerId);
        stats.put("totalOrders", sellerOrders.size());
        
        Map<Order.OrderStatus, Long> statusCounts = sellerOrders.stream()
                .collect(Collectors.groupingBy(Order::getStatus, Collectors.counting()));
        stats.put("statusCounts", statusCounts);
        
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        Double revenue = orderRepository.getSellerRevenueByDateRange(sellerId, thirtyDaysAgo, LocalDateTime.now());
        stats.put("revenueLastMonth", revenue != null ? revenue : 0.0);
        
        return stats;
    }

    @Override
    public Map<String, Object> getRevenueStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> stats = new HashMap<>();
        
        Double totalRevenue = orderRepository.getTotalRevenueByDateRange(startDate, endDate);
        stats.put("totalRevenue", totalRevenue != null ? totalRevenue : 0.0);
        
        List<Object[]> dailyCounts = orderRepository.getDailyOrderCounts(startDate, endDate);
        stats.put("dailyOrderCounts", dailyCounts);
        
        return stats;
    }

    @Override
    public Map<String, Object> getSellerRevenueStatistics(Long sellerId, LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> stats = new HashMap<>();
        
        Double totalRevenue = orderRepository.getSellerRevenueByDateRange(sellerId, startDate, endDate);
        stats.put("totalRevenue", totalRevenue != null ? totalRevenue : 0.0);
        
        List<OrderItem> items = orderItemRepository.findBySellerAndDateRange(sellerId, startDate, endDate);
        stats.put("totalItemsSold", items.stream().mapToInt(OrderItem::getQuantity).sum());
        
        return stats;
    }

    @Override
    public List<Map<String, Object>> getDailyOrderCounts(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> dailyCounts = orderRepository.getDailyOrderCounts(startDate, endDate);
        return dailyCounts.stream()
                .map(row -> {
                    Map<String, Object> dayData = new HashMap<>();
                    dayData.put("date", row[0]);
                    dayData.put("count", row[1]);
                    return dayData;
                })
                .collect(Collectors.toList());
    }

    @Override
    public boolean canCancelOrder(Long orderId, Long userId) {
        try {
            Order order = orderRepository.findById(orderId).orElse(null);
            return order != null && order.getUserId().equals(userId) && order.canBeCancelled();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean canReturnOrder(Long orderId, Long userId) {
        try {
            Order order = orderRepository.findById(orderId).orElse(null);
            return order != null && order.getUserId().equals(userId) && order.canBeReturned();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void processAutomaticStatusUpdates() {
        logger.info("Processing automatic status updates");
        
        // Auto-cancel pending orders older than 24 hours
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24);
        List<Order> pendingOrders = orderRepository.findPendingOrdersOlderThan(cutoffTime);
        
        for (Order order : pendingOrders) {
            order.updateStatus(Order.OrderStatus.CANCELLED, "Auto-cancelled due to timeout");
            orderRepository.save(order);
            logger.info("Auto-cancelled order {} due to timeout", order.getId());
        }
    }

    @Override
    public void cleanupOldOrders() {
        logger.info("Starting cleanup of old orders");
        // Implementation for cleaning up old orders if needed
    }

    @Override
    public OrderResponse updatePaymentStatus(Long orderId, Order.PaymentStatus paymentStatus, String transactionId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        order.setPaymentStatus(paymentStatus);
        if (transactionId != null) {
            order.setPaymentTransactionId(transactionId);
        }

        if (paymentStatus == Order.PaymentStatus.COMPLETED) {
            if (order.getStatus() == Order.OrderStatus.PENDING) {
                order.updateStatus(Order.OrderStatus.CONFIRMED, "Payment completed");
            }
        } else if (paymentStatus == Order.PaymentStatus.FAILED) {
            order.updateStatus(Order.OrderStatus.CANCELLED, "Payment failed");
        }

        order = orderRepository.save(order);
        logger.info("Payment status updated for order {} to {}", orderId, paymentStatus);
        
        return new OrderResponse(order);
    }

    @Override
    public OrderResponse processPaymentCallback(Long orderId, Map<String, Object> paymentData) {
        // Implementation for processing payment gateway callbacks
        logger.info("Processing payment callback for order {}", orderId);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        // Process payment data based on your payment gateway
        String status = (String) paymentData.get("status");
        String transactionId = (String) paymentData.get("transactionId");

        Order.PaymentStatus paymentStatus;
        switch (status.toLowerCase()) {
            case "success":
            case "completed":
                paymentStatus = Order.PaymentStatus.COMPLETED;
                break;
            case "failed":
            case "error":
                paymentStatus = Order.PaymentStatus.FAILED;
                break;
            default:
                paymentStatus = Order.PaymentStatus.PROCESSING;
        }

        return updatePaymentStatus(orderId, paymentStatus, transactionId);
    }

    // Helper methods
    private UserServiceClient.UserDto getUserDetails(Long userId) {
        try {
            UserServiceClient.UserDto user = userServiceClient.getUserById(userId).getBody();
            if (user == null) {
                throw new RuntimeException("User not found: " + userId);
            }
            return user;
        } catch (FeignException e) {
            logger.error("Error fetching user details for user {}: {}", userId, e.getMessage());
            // Return a default user object
            UserServiceClient.UserDto defaultUser = new UserServiceClient.UserDto();
            defaultUser.setId(userId);
            defaultUser.setEmail("unknown@example.com");
            defaultUser.setFirstName("Unknown");
            defaultUser.setLastName("User");
            return defaultUser;
        }
    }
} 