package com.example.item_service.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCategoryResponse {

    private String name;
    private String description;
    private String imageUrl;
    private Integer productCount;
    private List<String> subcategories;
    private boolean isActive;
    private String slug;
    private Integer sortOrder;
    private List<ProductCategoryResponse> children;
    private String parentCategory;
} 