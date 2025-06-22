package com.example.item_service.service.impl;

import com.example.item_service.dto.ProductCategoryResponse;
import com.example.item_service.repository.ItemRepository;
import com.example.item_service.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {

    @Autowired
    private ItemRepository itemRepository;

    // Predefined categories for the e-commerce platform
    private static final Map<String, List<String>> CATEGORY_HIERARCHY = Map.of(
        "Electronics", Arrays.asList("Smartphones", "Laptops", "Tablets", "Headphones", "Cameras", "Gaming", "Accessories"),
        "Clothing", Arrays.asList("Men's Wear", "Women's Wear", "Kids Wear", "Shoes", "Accessories", "Sportswear"),
        "Home & Garden", Arrays.asList("Furniture", "Kitchen", "Bathroom", "Garden", "Decor", "Storage", "Lighting"),
        "Books", Arrays.asList("Fiction", "Non-Fiction", "Educational", "Children's Books", "Comics", "Magazines"),
        "Sports", Arrays.asList("Fitness", "Outdoor", "Team Sports", "Water Sports", "Winter Sports", "Equipment"),
        "Beauty", Arrays.asList("Skincare", "Makeup", "Hair Care", "Fragrances", "Men's Grooming", "Tools"),
        "Automotive", Arrays.asList("Car Parts", "Motorcycle", "Tools", "Accessories", "Tires", "Electronics"),
        "Health", Arrays.asList("Supplements", "Medical", "Personal Care", "Fitness", "Wellness", "Baby Care")
    );

    @Override
    public List<ProductCategoryResponse> getAllCategories() {
        return CATEGORY_HIERARCHY.entrySet().stream()
                .map(entry -> {
                    String categoryName = entry.getKey();
                    Integer productCount = itemRepository.countByCategory(categoryName);
                    
                    return ProductCategoryResponse.builder()
                            .name(categoryName)
                            .description("Products in " + categoryName + " category")
                            .subcategories(entry.getValue())
                            .productCount(productCount)
                            .isActive(true)
                            .slug(categoryName.toLowerCase().replace(" ", "-").replace("&", "and"))
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductCategoryResponse> getActiveCategories() {
        return getAllCategories().stream()
                .filter(ProductCategoryResponse::isActive)
                .collect(Collectors.toList());
    }

    @Override
    public ProductCategoryResponse getCategoryByName(String name) {
        return getAllCategories().stream()
                .filter(category -> category.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<ProductCategoryResponse> getCategoriesWithProducts() {
        return getAllCategories().stream()
                .filter(category -> category.getProductCount() > 0)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getSubcategories(String parentCategory) {
        return CATEGORY_HIERARCHY.getOrDefault(parentCategory, Collections.emptyList());
    }

    @Override
    public List<ProductCategoryResponse> getCategoryHierarchy() {
        return getAllCategories().stream()
                .map(category -> {
                    List<ProductCategoryResponse> children = category.getSubcategories().stream()
                            .map(subcategory -> {
                                Integer subProductCount = itemRepository.countByCategoryContaining(subcategory);
                                return ProductCategoryResponse.builder()
                                        .name(subcategory)
                                        .description("Products in " + subcategory + " subcategory")
                                        .productCount(subProductCount)
                                        .parentCategory(category.getName())
                                        .isActive(true)
                                        .slug(subcategory.toLowerCase().replace(" ", "-").replace("'", ""))
                                        .build();
                            })
                            .collect(Collectors.toList());
                    
                    category.setChildren(children);
                    return category;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getPopularCategories(int limit) {
        List<Object[]> popularCategories = itemRepository.findTopCategoriesByProductCount(limit);
        return popularCategories.stream()
                .map(row -> (String) row[0])
                .collect(Collectors.toList());
    }

    @Override
    public List<String> searchCategories(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }
        
        String searchTerm = keyword.toLowerCase();
        Set<String> results = new HashSet<>();
        
        // Search in main categories
        CATEGORY_HIERARCHY.keySet().stream()
                .filter(category -> category.toLowerCase().contains(searchTerm))
                .forEach(results::add);
        
        // Search in subcategories
        CATEGORY_HIERARCHY.values().stream()
                .flatMap(List::stream)
                .filter(subcategory -> subcategory.toLowerCase().contains(searchTerm))
                .forEach(results::add);
        
        return new ArrayList<>(results);
    }
} 