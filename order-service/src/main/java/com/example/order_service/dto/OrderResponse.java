package com.example.order_service.dto;

import com.example.order_service.model.Order;
import com.example.order_service.model.OrderStatusHistory;
import com.example.order_service.model.ShippingAddress;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private Long id;
    private Long userId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private Order.OrderStatus status;
    private String statusDescription;
    private BigDecimal totalAmount;
    private BigDecimal shippingCost;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private List<OrderItemResponse> orderItems;
    private ShippingAddress shippingAddress;
    private ShippingAddress billingAddress;
    private Order.PaymentMethod paymentMethod;
    private Order.PaymentStatus paymentStatus;
    private String paymentTransactionId;
    private String notes;
    private String trackingNumber;
    private LocalDateTime estimatedDeliveryDate;
    private LocalDateTime actualDeliveryDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderStatusHistory> statusHistory;
    private int totalItems;
    private boolean canBeCancelled;
    private boolean canBeReturned;

    public OrderResponse(Order order) {
        this.id = order.getId();
        this.userId = order.getUserId();
        this.customerName = order.getCustomerName();
        this.customerEmail = order.getCustomerEmail();
        this.customerPhone = order.getCustomerPhone();
        this.status = order.getStatus();
        this.statusDescription = order.getStatus().getDescription();
        this.totalAmount = order.getTotalAmount();
        this.shippingCost = order.getShippingCost();
        this.taxAmount = order.getTaxAmount();
        this.discountAmount = order.getDiscountAmount();
        this.finalAmount = order.getFinalAmount();
        this.orderItems = order.getOrderItems().stream()
                .map(OrderItemResponse::new)
                .collect(Collectors.toList());
        this.shippingAddress = order.getShippingAddress();
        this.billingAddress = order.getBillingAddress();
        this.paymentMethod = order.getPaymentMethod();
        this.paymentStatus = order.getPaymentStatus();
        this.paymentTransactionId = order.getPaymentTransactionId();
        this.notes = order.getNotes();
        this.trackingNumber = order.getTrackingNumber();
        this.estimatedDeliveryDate = order.getEstimatedDeliveryDate();
        this.actualDeliveryDate = order.getActualDeliveryDate();
        this.createdAt = order.getCreatedAt();
        this.updatedAt = order.getUpdatedAt();
        this.statusHistory = order.getStatusHistory();
        this.totalItems = order.getTotalItems();
        this.canBeCancelled = order.canBeCancelled();
        this.canBeReturned = order.canBeReturned();
    }
} 