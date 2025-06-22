package com.example.item_service.service;

import com.example.item_service.dto.ProductCategoryResponse;

import java.util.List;

public interface ProductCategoryService {

    List<ProductCategoryResponse> getAllCategories();
    
    List<ProductCategoryResponse> getActiveCategories();
    
    ProductCategoryResponse getCategoryByName(String name);
    
    List<ProductCategoryResponse> getCategoriesWithProducts();
    
    List<String> getSubcategories(String parentCategory);
    
    List<ProductCategoryResponse> getCategoryHierarchy();
    
    List<String> getPopularCategories(int limit);
    
    List<String> searchCategories(String keyword);
} 