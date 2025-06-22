package com.example.cart_service.dto;

import com.example.cart_service.model.CartItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CartItemResponse {
    
    private Long id;
    private Long itemId;
    private String itemName;
    private String itemDescription;
    private String itemImageUrl;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;
    private Long sellerId;
    private String sellerName;
    private String productAttributes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public CartItemResponse() {}
    
    public CartItemResponse(CartItem cartItem) {
        this.id = cartItem.getId();
        this.itemId = cartItem.getItemId();
        this.itemName = cartItem.getItemName();
        this.itemDescription = cartItem.getItemDescription();
        this.itemImageUrl = cartItem.getItemImageUrl();
        this.price = cartItem.getPrice();
        this.quantity = cartItem.getQuantity();
        this.subtotal = cartItem.getSubtotal();
        this.sellerId = cartItem.getSellerId();
        this.sellerName = cartItem.getSellerName();
        this.productAttributes = cartItem.getProductAttributes();
        this.createdAt = cartItem.getCreatedAt();
        this.updatedAt = cartItem.getUpdatedAt();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getItemId() {
        return itemId;
    }
    
    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }
    
    public String getItemName() {
        return itemName;
    }
    
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    
    public String getItemDescription() {
        return itemDescription;
    }
    
    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }
    
    public String getItemImageUrl() {
        return itemImageUrl;
    }
    
    public void setItemImageUrl(String itemImageUrl) {
        this.itemImageUrl = itemImageUrl;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public BigDecimal getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
    
    public Long getSellerId() {
        return sellerId;
    }
    
    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }
    
    public String getSellerName() {
        return sellerName;
    }
    
    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }
    
    public String getProductAttributes() {
        return productAttributes;
    }
    
    public void setProductAttributes(String productAttributes) {
        this.productAttributes = productAttributes;
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
} 