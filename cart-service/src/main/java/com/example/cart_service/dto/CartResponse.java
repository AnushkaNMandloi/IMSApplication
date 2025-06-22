package com.example.cart_service.dto;

import com.example.cart_service.model.Cart;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CartResponse {
    
    private Long id;
    private Long userId;
    private String sessionId;
    private BigDecimal totalAmount;
    private Integer totalItems;
    private String status;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CartItemResponse> items;
    private boolean isGuest;
    private boolean isExpired;
    
    // Constructors
    public CartResponse() {}
    
    public CartResponse(Cart cart) {
        this.id = cart.getId();
        this.userId = cart.getUserId();
        this.sessionId = cart.getSessionId();
        this.totalAmount = cart.getTotalAmount();
        this.totalItems = cart.getTotalItems();
        this.status = cart.getStatus().name();
        this.expiresAt = cart.getExpiresAt();
        this.createdAt = cart.getCreatedAt();
        this.updatedAt = cart.getUpdatedAt();
        this.isGuest = cart.isGuest();
        this.isExpired = cart.isExpired();
        
        if (cart.getItems() != null) {
            this.items = cart.getItems().stream()
                    .map(CartItemResponse::new)
                    .collect(Collectors.toList());
        }
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public Integer getTotalItems() {
        return totalItems;
    }
    
    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public List<CartItemResponse> getItems() {
        return items;
    }
    
    public void setItems(List<CartItemResponse> items) {
        this.items = items;
    }
    
    public boolean isGuest() {
        return isGuest;
    }
    
    public void setGuest(boolean guest) {
        isGuest = guest;
    }
    
    public boolean isExpired() {
        return isExpired;
    }
    
    public void setExpired(boolean expired) {
        isExpired = expired;
    }
} 