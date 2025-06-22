package com.example.order_service.controller;

import com.example.order_service.dto.*;
import com.example.order_service.model.Order;
import com.example.order_service.service.OrderService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    // Order Creation
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('SELLER')")
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            OrderResponse order = orderService.createOrderFromCart(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(order);
        } catch (Exception e) {
            logger.error("Error creating order: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Get Order by ID
    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('USER') or hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> getOrder(
            @PathVariable Long orderId,
            Authentication authentication) {
        try {
            String role = authentication.getAuthorities().iterator().next().getAuthority();
            
            if ("ROLE_ADMIN".equals(role)) {
                // Admin can see any order
                OrderResponse order = orderService.getOrderById(orderId);
                return ResponseEntity.ok(order);
            } else {
                // Users can only see their own orders
                Long userId = Long.parseLong(authentication.getName());
                OrderResponse order = orderService.getOrderByIdAndUser(orderId, userId);
                return ResponseEntity.ok(order);
            }
        } catch (Exception e) {
            logger.error("Error fetching order {}: {}", orderId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Get User's Orders
    @GetMapping("/my-orders")
    @PreAuthorize("hasRole('USER') or hasRole('SELLER')")
    public ResponseEntity<List<OrderSummaryResponse>> getMyOrders(Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            List<OrderSummaryResponse> orders = orderService.getUserOrders(userId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            logger.error("Error fetching user orders: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get User's Orders with Pagination
    @GetMapping("/my-orders/paginated")
    @PreAuthorize("hasRole('USER') or hasRole('SELLER')")
    public ResponseEntity<Page<OrderSummaryResponse>> getMyOrdersPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            Pageable pageable = PageRequest.of(page, size);
            Page<OrderSummaryResponse> orders = orderService.getUserOrdersPaginated(userId, pageable);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            logger.error("Error fetching user orders: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get User's Orders by Status
    @GetMapping("/my-orders/status/{status}")
    @PreAuthorize("hasRole('USER') or hasRole('SELLER')")
    public ResponseEntity<List<OrderSummaryResponse>> getMyOrdersByStatus(
            @PathVariable Order.OrderStatus status,
            Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            List<OrderSummaryResponse> orders = orderService.getUserOrdersByStatus(userId, status);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            logger.error("Error fetching user orders by status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Order Status Management
    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        try {
            OrderResponse order = orderService.updateOrderStatus(orderId, request);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            logger.error("Error updating order status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Cancel Order
    @PostMapping("/{orderId}/cancel")
    @PreAuthorize("hasRole('USER') or hasRole('SELLER')")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable Long orderId,
            @RequestParam(required = false) String reason,
            Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            OrderResponse order = orderService.cancelOrder(orderId, userId, reason);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            logger.error("Error cancelling order: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Request Return
    @PostMapping("/{orderId}/return")
    @PreAuthorize("hasRole('USER') or hasRole('SELLER')")
    public ResponseEntity<OrderResponse> requestReturn(
            @PathVariable Long orderId,
            @RequestParam(required = false) String reason,
            Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            OrderResponse order = orderService.requestReturn(orderId, userId, reason);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            logger.error("Error requesting return: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Confirm Order (Admin/Seller)
    @PostMapping("/{orderId}/confirm")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    public ResponseEntity<OrderResponse> confirmOrder(
            @PathVariable Long orderId,
            @RequestParam(required = false) String reason) {
        try {
            OrderResponse order = orderService.confirmOrder(orderId, reason);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            logger.error("Error confirming order: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Ship Order (Admin/Seller)
    @PostMapping("/{orderId}/ship")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    public ResponseEntity<OrderResponse> shipOrder(
            @PathVariable Long orderId,
            @RequestParam String trackingNumber,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime estimatedDeliveryDate) {
        try {
            OrderResponse order = orderService.shipOrder(orderId, trackingNumber, estimatedDeliveryDate);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            logger.error("Error shipping order: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Deliver Order (Admin/Seller)
    @PostMapping("/{orderId}/deliver")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    public ResponseEntity<OrderResponse> deliverOrder(
            @PathVariable Long orderId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime actualDeliveryDate) {
        try {
            OrderResponse order = orderService.deliverOrder(orderId, actualDeliveryDate);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            logger.error("Error delivering order: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Track Order by Tracking Number
    @GetMapping("/track/{trackingNumber}")
    public ResponseEntity<OrderResponse> trackOrder(@PathVariable String trackingNumber) {
        try {
            OrderResponse order = orderService.trackOrder(trackingNumber);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            logger.error("Error tracking order: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Admin: Get Orders by Status
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderSummaryResponse>> getOrdersByStatus(
            @PathVariable Order.OrderStatus status) {
        try {
            List<OrderSummaryResponse> orders = orderService.getOrdersByStatus(status);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            logger.error("Error fetching orders by status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Admin: Get Orders by Status with Pagination
    @GetMapping("/status/{status}/paginated")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<OrderSummaryResponse>> getOrdersByStatusPaginated(
            @PathVariable Order.OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<OrderSummaryResponse> orders = orderService.getOrdersByStatusPaginated(status, pageable);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            logger.error("Error fetching orders by status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Seller: Get Seller Orders
    @GetMapping("/seller/my-orders")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<List<OrderSummaryResponse>> getSellerOrders(Authentication authentication) {
        try {
            Long sellerId = Long.parseLong(authentication.getName());
            List<OrderSummaryResponse> orders = orderService.getSellerOrders(sellerId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            logger.error("Error fetching seller orders: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Seller: Get Seller Orders with Pagination
    @GetMapping("/seller/my-orders/paginated")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Page<OrderSummaryResponse>> getSellerOrdersPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        try {
            Long sellerId = Long.parseLong(authentication.getName());
            Pageable pageable = PageRequest.of(page, size);
            Page<OrderSummaryResponse> orders = orderService.getSellerOrdersPaginated(sellerId, pageable);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            logger.error("Error fetching seller orders: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Seller: Get Seller Orders by Status
    @GetMapping("/seller/my-orders/status/{status}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<List<OrderSummaryResponse>> getSellerOrdersByStatus(
            @PathVariable Order.OrderStatus status,
            Authentication authentication) {
        try {
            Long sellerId = Long.parseLong(authentication.getName());
            List<OrderSummaryResponse> orders = orderService.getSellerOrdersByStatus(sellerId, status);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            logger.error("Error fetching seller orders by status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Analytics and Statistics
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getOrderStatistics() {
        try {
            Map<String, Object> stats = orderService.getOrderStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Error fetching order statistics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Seller Statistics
    @GetMapping("/seller/statistics")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Map<String, Object>> getSellerOrderStatistics(Authentication authentication) {
        try {
            Long sellerId = Long.parseLong(authentication.getName());
            Map<String, Object> stats = orderService.getSellerOrderStatistics(sellerId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Error fetching seller order statistics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Revenue Statistics
    @GetMapping("/revenue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getRevenueStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            Map<String, Object> stats = orderService.getRevenueStatistics(startDate, endDate);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Error fetching revenue statistics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Seller Revenue Statistics
    @GetMapping("/seller/revenue")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Map<String, Object>> getSellerRevenueStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Authentication authentication) {
        try {
            Long sellerId = Long.parseLong(authentication.getName());
            Map<String, Object> stats = orderService.getSellerRevenueStatistics(sellerId, startDate, endDate);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Error fetching seller revenue statistics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Daily Order Counts
    @GetMapping("/daily-counts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getDailyOrderCounts(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            List<Map<String, Object>> counts = orderService.getDailyOrderCounts(startDate, endDate);
            return ResponseEntity.ok(counts);
        } catch (Exception e) {
            logger.error("Error fetching daily order counts: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Check if order can be cancelled
    @GetMapping("/{orderId}/can-cancel")
    @PreAuthorize("hasRole('USER') or hasRole('SELLER')")
    public ResponseEntity<Boolean> canCancelOrder(
            @PathVariable Long orderId,
            Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            boolean canCancel = orderService.canCancelOrder(orderId, userId);
            return ResponseEntity.ok(canCancel);
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }

    // Check if order can be returned
    @GetMapping("/{orderId}/can-return")
    @PreAuthorize("hasRole('USER') or hasRole('SELLER')")
    public ResponseEntity<Boolean> canReturnOrder(
            @PathVariable Long orderId,
            Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            boolean canReturn = orderService.canReturnOrder(orderId, userId);
            return ResponseEntity.ok(canReturn);
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }

    // Payment Status Update (for payment gateway callbacks)
    @PostMapping("/{orderId}/payment-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> updatePaymentStatus(
            @PathVariable Long orderId,
            @RequestParam Order.PaymentStatus paymentStatus,
            @RequestParam(required = false) String transactionId) {
        try {
            OrderResponse order = orderService.updatePaymentStatus(orderId, paymentStatus, transactionId);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            logger.error("Error updating payment status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Payment Callback (webhook endpoint)
    @PostMapping("/{orderId}/payment-callback")
    public ResponseEntity<OrderResponse> paymentCallback(
            @PathVariable Long orderId,
            @RequestBody Map<String, Object> paymentData) {
        try {
            OrderResponse order = orderService.processPaymentCallback(orderId, paymentData);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            logger.error("Error processing payment callback: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Health check endpoint
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> health = Map.of(
                "status", "UP",
                "service", "order-service",
                "timestamp", LocalDateTime.now().toString()
        );
        return ResponseEntity.ok(health);
    }
} 