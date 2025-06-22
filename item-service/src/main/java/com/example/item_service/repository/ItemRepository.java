package com.example.item_service.repository;

import com.example.item_service.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    
    Item findByItemName(String itemName);
    
    // Enhanced search methods
    List<Item> findByItemNameContainingIgnoreCase(String name);
    
    List<Item> findByCategoryIgnoreCase(String category);
    
    List<Item> findBySellerIdOrderByCreatedAtDesc(Long sellerId);
    
    List<Item> findByQuantityGreaterThan(Integer quantity);
    
    List<Item> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    List<Item> findByCategoryAndPriceBetween(String category, BigDecimal minPrice, BigDecimal maxPrice);
    
    // Advanced search with pagination
    Page<Item> findByItemNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String name, String description, Pageable pageable);
    
    Page<Item> findByCategoryIgnoreCase(String category, Pageable pageable);
    
    Page<Item> findBySellerIdOrderByCreatedAtDesc(Long sellerId, Pageable pageable);
    
    // Category-related queries
    Integer countByCategory(String category);
    
    Integer countByCategoryContaining(String categoryKeyword);
    
    @Query("SELECT i.category, COUNT(i) FROM Item i GROUP BY i.category ORDER BY COUNT(i) DESC")
    List<Object[]> findTopCategoriesByProductCount(@Param("limit") int limit);
    
    // Stock and availability queries
    List<Item> findByQuantityLessThanEqual(Integer threshold);
    
    @Query("SELECT i FROM Item i WHERE i.quantity > 0 ORDER BY i.createdAt DESC")
    List<Item> findAvailableItems();
    
    @Query("SELECT i FROM Item i WHERE i.quantity > 0 ORDER BY i.createdAt DESC")
    Page<Item> findAvailableItems(Pageable pageable);
    
    // Advanced search query
    @Query("SELECT i FROM Item i WHERE " +
           "(:keyword IS NULL OR LOWER(i.itemName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:category IS NULL OR LOWER(i.category) = LOWER(:category)) AND " +
           "(:sellerId IS NULL OR i.sellerId = :sellerId) AND " +
           "(:minPrice IS NULL OR i.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR i.price <= :maxPrice) AND " +
           "(:inStock IS NULL OR (:inStock = true AND i.quantity > 0) OR (:inStock = false))")
    Page<Item> searchItems(@Param("keyword") String keyword,
                          @Param("category") String category,
                          @Param("sellerId") Long sellerId,
                          @Param("minPrice") BigDecimal minPrice,
                          @Param("maxPrice") BigDecimal maxPrice,
                          @Param("inStock") Boolean inStock,
                          Pageable pageable);
    
    // Seller statistics
    @Query("SELECT COUNT(i) FROM Item i WHERE i.sellerId = :sellerId")
    Long countBySellerId(@Param("sellerId") Long sellerId);
    
    @Query("SELECT SUM(i.quantity) FROM Item i WHERE i.sellerId = :sellerId")
    Long getTotalInventoryBySeller(@Param("sellerId") Long sellerId);
    
    @Query("SELECT i.category, COUNT(i) FROM Item i WHERE i.sellerId = :sellerId GROUP BY i.category")
    List<Object[]> getCategoryDistributionBySeller(@Param("sellerId") Long sellerId);
    
    // Featured and popular items
    @Query("SELECT i FROM Item i WHERE i.imageUrl IS NOT NULL AND i.quantity > 0 ORDER BY i.createdAt DESC")
    List<Item> findFeaturedItems(Pageable pageable);
    
    // Low stock alerts
    @Query("SELECT i FROM Item i WHERE i.quantity <= :threshold AND i.sellerId = :sellerId")
    List<Item> findLowStockItemsBySeller(@Param("sellerId") Long sellerId, @Param("threshold") Integer threshold);
}
