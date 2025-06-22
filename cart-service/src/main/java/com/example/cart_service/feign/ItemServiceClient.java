package com.example.cart_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.List;

@FeignClient(name = "item-service", path = "/api/items")
public interface ItemServiceClient {
    
    @GetMapping("/{id}")
    ResponseEntity<ItemDto> getItemById(@PathVariable("id") Long id);
    
    @PostMapping("/batch")
    ResponseEntity<List<ItemDto>> getItemsByIds(@RequestBody List<Long> ids);
    
    @GetMapping("/{id}/availability")
    ResponseEntity<ItemAvailabilityDto> checkItemAvailability(@PathVariable("id") Long id);
    
    @PostMapping("/reserve")
    ResponseEntity<Void> reserveItems(@RequestBody List<ItemReservationDto> reservations);
    
    @PostMapping("/release")
    ResponseEntity<Void> releaseReservations(@RequestBody List<Long> reservationIds);
    
    // DTOs for communication with item-service
    class ItemDto {
        private Long id;
        private String name;
        private String description;
        private BigDecimal price;
        private Integer stockQuantity;
        private String imageUrl;
        private Long sellerId;
        private String sellerName;
        private String category;
        private String status;
        
        // Constructors
        public ItemDto() {}
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
        
        public Integer getStockQuantity() { return stockQuantity; }
        public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
        
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        
        public Long getSellerId() { return sellerId; }
        public void setSellerId(Long sellerId) { this.sellerId = sellerId; }
        
        public String getSellerName() { return sellerName; }
        public void setSellerName(String sellerName) { this.sellerName = sellerName; }
        
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
    
    class ItemAvailabilityDto {
        private Long itemId;
        private boolean available;
        private Integer availableQuantity;
        private String message;
        
        // Constructors
        public ItemAvailabilityDto() {}
        
        // Getters and Setters
        public Long getItemId() { return itemId; }
        public void setItemId(Long itemId) { this.itemId = itemId; }
        
        public boolean isAvailable() { return available; }
        public void setAvailable(boolean available) { this.available = available; }
        
        public Integer getAvailableQuantity() { return availableQuantity; }
        public void setAvailableQuantity(Integer availableQuantity) { this.availableQuantity = availableQuantity; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
    
    class ItemReservationDto {
        private Long itemId;
        private Integer quantity;
        private String reservationId;
        
        // Constructors
        public ItemReservationDto() {}
        
        public ItemReservationDto(Long itemId, Integer quantity, String reservationId) {
            this.itemId = itemId;
            this.quantity = quantity;
            this.reservationId = reservationId;
        }
        
        // Getters and Setters
        public Long getItemId() { return itemId; }
        public void setItemId(Long itemId) { this.itemId = itemId; }
        
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        
        public String getReservationId() { return reservationId; }
        public void setReservationId(String reservationId) { this.reservationId = reservationId; }
    }
} 