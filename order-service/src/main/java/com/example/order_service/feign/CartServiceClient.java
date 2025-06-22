package com.example.order_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@FeignClient(name = "cart-service", path = "/cart")
public interface CartServiceClient {

    @GetMapping("/{cartId}")
    ResponseEntity<CartDto> getCartById(@PathVariable Long cartId);

    @PostMapping("/{cartId}/convert")
    ResponseEntity<Void> markCartAsConverted(@PathVariable Long cartId);

    @DeleteMapping("/{cartId}")
    ResponseEntity<Void> clearCart(@PathVariable Long cartId);

    // DTOs for cart service communication
    class CartDto {
        private Long id;
        private Long userId;
        private String sessionId;
        private List<CartItemDto> items;
        private Integer totalItems;
        private BigDecimal totalAmount;
        private LocalDateTime expiresAt;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        public List<CartItemDto> getItems() { return items; }
        public void setItems(List<CartItemDto> items) { this.items = items; }
        public Integer getTotalItems() { return totalItems; }
        public void setTotalItems(Integer totalItems) { this.totalItems = totalItems; }
        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
        public LocalDateTime getExpiresAt() { return expiresAt; }
        public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    }

    class CartItemDto {
        private Long id;
        private Long itemId;
        private String itemName;
        private String itemDescription;
        private BigDecimal price;
        private Integer quantity;
        private BigDecimal subtotal;
        private Long sellerId;
        private String sellerName;
        private String imageUrl;
        private String category;
        private String productAttributes;

        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getItemId() { return itemId; }
        public void setItemId(Long itemId) { this.itemId = itemId; }
        public String getItemName() { return itemName; }
        public void setItemName(String itemName) { this.itemName = itemName; }
        public String getItemDescription() { return itemDescription; }
        public void setItemDescription(String itemDescription) { this.itemDescription = itemDescription; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public BigDecimal getSubtotal() { return subtotal; }
        public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
        public Long getSellerId() { return sellerId; }
        public void setSellerId(Long sellerId) { this.sellerId = sellerId; }
        public String getSellerName() { return sellerName; }
        public void setSellerName(String sellerName) { this.sellerName = sellerName; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getProductAttributes() { return productAttributes; }
        public void setProductAttributes(String productAttributes) { this.productAttributes = productAttributes; }
    }
} 