package com.example.item_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkProductUploadRequest {

    @NotNull(message = "Seller ID is required")
    private Long sellerId;

    @NotEmpty(message = "Products list cannot be empty")
    @Valid
    private List<BulkProductItem> products;

    private boolean validateOnly = false; // If true, only validate without saving

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BulkProductItem {
        private String name;
        private String description;
        private String category;
        private String subcategory;
        private String brand;
        private String price;
        private String quantity;
        private String imageUrl;
        private String tags; // comma-separated
        private String sku;
        private String weight;
        private String dimensions;
        private String color;
        private String size;
        private String material;
        private String warranty;
        private String minOrderQuantity;
        private String maxOrderQuantity;
        private String discountPercentage;
        private String isActive;
        private String isFeatured;
        private String metaTitle;
        private String metaDescription;
        private String keywords;
    }
} 