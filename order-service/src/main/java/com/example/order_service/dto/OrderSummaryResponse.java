package com.example.order_service.dto;

import com.example.order_service.model.Order;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderSummaryResponse {

    private Long id;
    private Long userId;
    private Order.OrderStatus status;
    private String statusDescription;
    private BigDecimal finalAmount;
    private int totalItems;
    private LocalDateTime createdAt;
    private LocalDateTime estimatedDeliveryDate;
    private String trackingNumber;
    private boolean canBeCancelled;
    private boolean canBeReturned;

    public OrderSummaryResponse(Order order) {
        this.id = order.getId();
        this.userId = order.getUserId();
        this.status = order.getStatus();
        this.statusDescription = order.getStatus().getDescription();
        this.finalAmount = order.getFinalAmount();
        this.totalItems = order.getTotalItems();
        this.createdAt = order.getCreatedAt();
        this.estimatedDeliveryDate = order.getEstimatedDeliveryDate();
        this.trackingNumber = order.getTrackingNumber();
        this.canBeCancelled = order.canBeCancelled();
        this.canBeReturned = order.canBeReturned();
    }
} 