package com.example.order_service.dto;

import com.example.order_service.model.OrderItem;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponse {

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

    public OrderItemResponse(OrderItem orderItem) {
        this.id = orderItem.getId();
        this.itemId = orderItem.getItemId();
        this.itemName = orderItem.getItemName();
        this.itemDescription = orderItem.getItemDescription();
        this.price = orderItem.getPrice();
        this.quantity = orderItem.getQuantity();
        this.subtotal = orderItem.getSubtotal();
        this.sellerId = orderItem.getSellerId();
        this.sellerName = orderItem.getSellerName();
        this.imageUrl = orderItem.getImageUrl();
        this.category = orderItem.getCategory();
        this.productAttributes = orderItem.getProductAttributes();
    }
} 