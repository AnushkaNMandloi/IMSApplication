package com.example.order_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderStatusHistory {

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Order.OrderStatus status;

    @Size(max = 500, message = "Reason must not exceed 500 characters")
    @Column(name = "reason")
    private String reason;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Size(max = 255, message = "Changed by must not exceed 255 characters")
    @Column(name = "changed_by")
    private String changedBy;
} 