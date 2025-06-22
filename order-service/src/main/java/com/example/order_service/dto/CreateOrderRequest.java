package com.example.order_service.dto;

import com.example.order_service.model.Order;
import com.example.order_service.model.ShippingAddress;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderRequest {

    @NotNull(message = "Cart ID is required")
    private Long cartId;

    @Valid
    @NotNull(message = "Shipping address is required")
    private ShippingAddress shippingAddress;

    @Valid
    private ShippingAddress billingAddress;

    @NotNull(message = "Payment method is required")
    private Order.PaymentMethod paymentMethod;

    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;

    @DecimalMin(value = "0.0", message = "Shipping cost cannot be negative")
    private BigDecimal shippingCost = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Tax amount cannot be negative")
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Discount amount cannot be negative")
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Size(max = 100, message = "Discount code must not exceed 100 characters")
    private String discountCode;

    private boolean useBillingAddressForShipping = false;
} 