package com.example.order_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"orderItems"})
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User ID is required")
    @Column(nullable = false)
    private Long userId;

    @Size(max = 255, message = "Customer name must not exceed 255 characters")
    private String customerName;

    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String customerEmail;

    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String customerPhone;

    @NotNull(message = "Order status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total amount must be greater than 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Shipping cost cannot be negative")
    @Column(precision = 10, scale = 2)
    private BigDecimal shippingCost = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Tax amount cannot be negative")
    @Column(precision = 10, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Discount amount cannot be negative")
    @Column(precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal finalAmount = BigDecimal.ZERO;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Embedded
    private ShippingAddress shippingAddress;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "addressLine1", column = @Column(name = "billing_address_line1")),
        @AttributeOverride(name = "addressLine2", column = @Column(name = "billing_address_line2")),
        @AttributeOverride(name = "city", column = @Column(name = "billing_city")),
        @AttributeOverride(name = "state", column = @Column(name = "billing_state")),
        @AttributeOverride(name = "zipCode", column = @Column(name = "billing_zip_code")),
        @AttributeOverride(name = "country", column = @Column(name = "billing_country"))
    })
    private ShippingAddress billingAddress;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Size(max = 255, message = "Payment transaction ID must not exceed 255 characters")
    private String paymentTransactionId;

    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;

    @Size(max = 255, message = "Tracking number must not exceed 255 characters")
    private String trackingNumber;

    private LocalDateTime estimatedDeliveryDate;

    private LocalDateTime actualDeliveryDate;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Size(max = 255, message = "Created by must not exceed 255 characters")
    private String createdBy;

    @Size(max = 255, message = "Updated by must not exceed 255 characters")
    private String updatedBy;

    // Order status tracking
    @ElementCollection
    @CollectionTable(name = "order_status_history", joinColumns = @JoinColumn(name = "order_id"))
    private List<OrderStatusHistory> statusHistory = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        calculateFinalAmount();
        addStatusHistory(status, "Order created");
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateFinalAmount();
    }

    // Business logic methods
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
        calculateTotalAmount();
    }

    public void removeOrderItem(OrderItem orderItem) {
        orderItems.remove(orderItem);
        orderItem.setOrder(null);
        calculateTotalAmount();
    }

    public void calculateTotalAmount() {
        totalAmount = orderItems.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        calculateFinalAmount();
    }

    public void calculateFinalAmount() {
        finalAmount = totalAmount
                .add(shippingCost != null ? shippingCost : BigDecimal.ZERO)
                .add(taxAmount != null ? taxAmount : BigDecimal.ZERO)
                .subtract(discountAmount != null ? discountAmount : BigDecimal.ZERO);
    }

    public void updateStatus(OrderStatus newStatus, String reason) {
        if (this.status != newStatus) {
            addStatusHistory(newStatus, reason);
            this.status = newStatus;
        }
    }

    private void addStatusHistory(OrderStatus status, String reason) {
        OrderStatusHistory history = new OrderStatusHistory();
        history.setStatus(status);
        history.setReason(reason);
        history.setTimestamp(LocalDateTime.now());
        statusHistory.add(history);
    }

    public boolean canBeCancelled() {
        return status == OrderStatus.PENDING || status == OrderStatus.CONFIRMED;
    }

    public boolean canBeReturned() {
        return status == OrderStatus.DELIVERED && 
               actualDeliveryDate != null && 
               actualDeliveryDate.isAfter(LocalDateTime.now().minusDays(30));
    }

    public int getTotalItems() {
        return orderItems.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
    }

    // Enums
    public enum OrderStatus {
        PENDING("Order placed, awaiting confirmation"),
        CONFIRMED("Order confirmed, processing"),
        PROCESSING("Order being prepared"),
        SHIPPED("Order shipped"),
        OUT_FOR_DELIVERY("Out for delivery"),
        DELIVERED("Order delivered"),
        CANCELLED("Order cancelled"),
        RETURNED("Order returned"),
        REFUNDED("Order refunded");

        private final String description;

        OrderStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum PaymentMethod {
        CREDIT_CARD("Credit Card"),
        DEBIT_CARD("Debit Card"),
        PAYPAL("PayPal"),
        STRIPE("Stripe"),
        CASH_ON_DELIVERY("Cash on Delivery"),
        BANK_TRANSFER("Bank Transfer");

        private final String displayName;

        PaymentMethod(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum PaymentStatus {
        PENDING("Payment pending"),
        PROCESSING("Payment processing"),
        COMPLETED("Payment completed"),
        FAILED("Payment failed"),
        REFUNDED("Payment refunded"),
        CANCELLED("Payment cancelled");

        private final String description;

        PaymentStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
} 