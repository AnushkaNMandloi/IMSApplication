package com.example.cart_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AddToCartRequest {
    
    @NotNull(message = "Item ID is required")
    private Long itemId;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    
    private String productAttributes; // JSON string for variants
    
    // Constructors
    public AddToCartRequest() {}
    
    public AddToCartRequest(Long itemId, Integer quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }
    
    public AddToCartRequest(Long itemId, Integer quantity, String productAttributes) {
        this.itemId = itemId;
        this.quantity = quantity;
        this.productAttributes = productAttributes;
    }
    
    // Getters and Setters
    public Long getItemId() {
        return itemId;
    }
    
    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public String getProductAttributes() {
        return productAttributes;
    }
    
    public void setProductAttributes(String productAttributes) {
        this.productAttributes = productAttributes;
    }
} 