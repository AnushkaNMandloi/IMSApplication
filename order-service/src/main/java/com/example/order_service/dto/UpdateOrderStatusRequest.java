package com.example.order_service.dto;

import com.example.order_service.model.Order;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateOrderStatusRequest {

    @NotNull(message = "Order status is required")
    private Order.OrderStatus status;

    @Size(max = 500, message = "Reason must not exceed 500 characters")
    private String reason;

    @Size(max = 255, message = "Tracking number must not exceed 255 characters")
    private String trackingNumber;
} 