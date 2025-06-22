package com.example.item_service.service;

import com.example.item_service.dto.BulkProductUploadRequest;
import com.example.item_service.dto.ProductSearchRequest;
import com.example.item_service.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public interface ItemService {

    Item addItem(Item item);
    Item getItemById(Long id);
    String deleteItem(Long id);
    List<Item> getAllItems();
    Item getItemByName(String itemName);
    Item updateItem(Item item);
    
    // Enhanced search methods
    List<Item> searchItems(String name, String category, Long sellerId);
    Page<Item> searchItemsAdvanced(ProductSearchRequest searchRequest);
    List<Item> findItemsByCategory(String category);
    Page<Item> findItemsByCategory(String category, Pageable pageable);
    List<Item> findItemsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
    
    // Cart-related methods
    List<Item> findItemsByIds(List<Long> itemIds);
    boolean checkItemAvailability(Long itemId, Integer quantity);
    void reserveItems(List<Long> itemIds, List<Integer> quantities);
    void releaseItems(List<Long> itemIds, List<Integer> quantities);
    
    // Seller management
    List<Item> findItemsBySeller(Long sellerId);
    Page<Item> findItemsBySeller(Long sellerId, Pageable pageable);
    List<Item> findLowStockItems(Long sellerId, Integer threshold);
    Map<String, Object> getSellerStatistics(Long sellerId);
    
    // Inventory management
    List<Item> findAvailableItems();
    Page<Item> findAvailableItems(Pageable pageable);
    List<Item> findFeaturedItems(int limit);
    void updateStock(Long itemId, Integer newQuantity);
    void adjustStock(Long itemId, Integer adjustment);
    
    // Bulk operations
    Map<String, Object> bulkUploadProducts(BulkProductUploadRequest request);
    List<String> validateBulkUpload(BulkProductUploadRequest request);
    void bulkUpdatePrices(Long sellerId, Map<Long, BigDecimal> priceUpdates);
    void bulkUpdateStock(Long sellerId, Map<Long, Integer> stockUpdates);
    
    // Analytics
    Map<String, Object> getProductStatistics();
    List<Map<String, Object>> getCategoryStatistics();
    List<Item> getTopSellingItems(int limit);
    List<Item> getRecentlyAddedItems(int limit);
}
