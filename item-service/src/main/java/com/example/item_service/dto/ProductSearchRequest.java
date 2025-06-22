package com.example.item_service.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSearchRequest {

    private String keyword;
    private String category;
    private List<String> categories;
    private Long sellerId;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer minQuantity;
    private Boolean inStock;
    private String sortBy; // name, price, createdAt, popularity
    private String sortDirection; // asc, desc
    private Integer page;
    private Integer size;
    private List<String> tags;
    private String brand;
    private Double minRating;
} 