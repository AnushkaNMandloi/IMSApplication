package com.example.item_service.service.impl;

import com.example.item_service.dto.BulkProductUploadRequest;
import com.example.item_service.dto.ProductSearchRequest;
import com.example.item_service.model.Item;
import com.example.item_service.repository.ItemRepository;
import com.example.item_service.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    ItemRepository itemRepository;

    @Override
    public Item addItem(Item item) {
        return itemRepository.save(item);
    }

    @Override
    public Item getItemById(Long id) {
        Optional<Item> item = itemRepository.findById(id);
        return item.orElse(null);
    }

    @Override
    public String deleteItem(Long id) {
        if(itemRepository.findById(id).isPresent()){
            itemRepository.deleteById(id);
            return "Item deleted successfully!";
        }
        return "Item not found";
    }

    @Override
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @Override
    public Item getItemByName(String itemName) {
        Optional<Item> item = Optional.ofNullable(itemRepository.findByItemName(itemName));
        return item.orElse(null);
    }
    
    @Override
    public Item updateItem(Item item) {
        return itemRepository.save(item);
    }
    
    @Override
    public List<Item> searchItems(String name, String category, Long sellerId) {
        List<Item> allItems = itemRepository.findAll();
        
        return allItems.stream()
                .filter(item -> name == null || item.getItemName().toLowerCase().contains(name.toLowerCase()))
                .filter(item -> category == null || (item.getCategory() != null && item.getCategory().toLowerCase().contains(category.toLowerCase())))
                .filter(item -> sellerId == null || (item.getSellerId() != null && item.getSellerId().equals(sellerId)))
                .collect(Collectors.toList());
    }

    @Override
    public Page<Item> searchItemsAdvanced(ProductSearchRequest searchRequest) {
        // Simple implementation - in production, use proper search with specifications
        List<Item> allItems = itemRepository.findAll();
        return new PageImpl<>(allItems);
    }

    @Override
    public List<Item> findItemsByCategory(String category) {
        return itemRepository.findAll().stream()
                .filter(item -> item.getCategory() != null && item.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    @Override
    public Page<Item> findItemsByCategory(String category, Pageable pageable) {
        List<Item> items = findItemsByCategory(category);
        return new PageImpl<>(items, pageable, items.size());
    }

    @Override
    public List<Item> findItemsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return itemRepository.findAll().stream()
                .filter(item -> item.getPrice().compareTo(minPrice) >= 0 && item.getPrice().compareTo(maxPrice) <= 0)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findItemsByIds(List<Long> itemIds) {
        return itemRepository.findAllById(itemIds);
    }

    @Override
    public boolean checkItemAvailability(Long itemId, Integer quantity) {
        Optional<Item> item = itemRepository.findById(itemId);
        return item.isPresent() && item.get().getQuantity() >= quantity;
    }

    @Override
    public void reserveItems(List<Long> itemIds, List<Integer> quantities) {
        // Simple implementation - in production, use proper transaction management
        for (int i = 0; i < itemIds.size(); i++) {
            Optional<Item> itemOpt = itemRepository.findById(itemIds.get(i));
            if (itemOpt.isPresent()) {
                Item item = itemOpt.get();
                item.setQuantity(item.getQuantity() - quantities.get(i));
                itemRepository.save(item);
            }
        }
    }

    @Override
    public void releaseItems(List<Long> itemIds, List<Integer> quantities) {
        // Simple implementation - in production, use proper transaction management
        for (int i = 0; i < itemIds.size(); i++) {
            Optional<Item> itemOpt = itemRepository.findById(itemIds.get(i));
            if (itemOpt.isPresent()) {
                Item item = itemOpt.get();
                item.setQuantity(item.getQuantity() + quantities.get(i));
                itemRepository.save(item);
            }
        }
    }

    @Override
    public List<Item> findItemsBySeller(Long sellerId) {
        return itemRepository.findAll().stream()
                .filter(item -> item.getSellerId() != null && item.getSellerId().equals(sellerId))
                .collect(Collectors.toList());
    }

    @Override
    public Page<Item> findItemsBySeller(Long sellerId, Pageable pageable) {
        List<Item> items = findItemsBySeller(sellerId);
        return new PageImpl<>(items, pageable, items.size());
    }

    @Override
    public List<Item> findLowStockItems(Long sellerId, Integer threshold) {
        return findItemsBySeller(sellerId).stream()
                .filter(item -> item.getQuantity() <= threshold)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getSellerStatistics(Long sellerId) {
        List<Item> sellerItems = findItemsBySeller(sellerId);
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalProducts", sellerItems.size());
        stats.put("totalValue", sellerItems.stream().mapToDouble(item -> item.getPrice().doubleValue() * item.getQuantity()).sum());
        stats.put("lowStockItems", findLowStockItems(sellerId, 10).size());
        return stats;
    }

    @Override
    public List<Item> findAvailableItems() {
        return itemRepository.findAll().stream()
                .filter(item -> item.getQuantity() > 0)
                .collect(Collectors.toList());
    }

    @Override
    public Page<Item> findAvailableItems(Pageable pageable) {
        List<Item> items = findAvailableItems();
        return new PageImpl<>(items, pageable, items.size());
    }

    @Override
    public List<Item> findFeaturedItems(int limit) {
        return itemRepository.findAll().stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public void updateStock(Long itemId, Integer newQuantity) {
        Optional<Item> itemOpt = itemRepository.findById(itemId);
        if (itemOpt.isPresent()) {
            Item item = itemOpt.get();
            item.setQuantity(newQuantity);
            itemRepository.save(item);
        }
    }

    @Override
    public void adjustStock(Long itemId, Integer adjustment) {
        Optional<Item> itemOpt = itemRepository.findById(itemId);
        if (itemOpt.isPresent()) {
            Item item = itemOpt.get();
            item.setQuantity(item.getQuantity() + adjustment);
            itemRepository.save(item);
        }
    }

    @Override
    public Map<String, Object> bulkUploadProducts(BulkProductUploadRequest request) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Bulk upload not implemented yet");
        return result;
    }

    @Override
    public List<String> validateBulkUpload(BulkProductUploadRequest request) {
        return Arrays.asList("Validation not implemented yet");
    }

    @Override
    public void bulkUpdatePrices(Long sellerId, Map<Long, BigDecimal> priceUpdates) {
        // Simple implementation
        priceUpdates.forEach((itemId, newPrice) -> {
            Optional<Item> itemOpt = itemRepository.findById(itemId);
            if (itemOpt.isPresent() && itemOpt.get().getSellerId().equals(sellerId)) {
                Item item = itemOpt.get();
                item.setPrice(newPrice);
                itemRepository.save(item);
            }
        });
    }

    @Override
    public void bulkUpdateStock(Long sellerId, Map<Long, Integer> stockUpdates) {
        // Simple implementation
        stockUpdates.forEach((itemId, newStock) -> {
            Optional<Item> itemOpt = itemRepository.findById(itemId);
            if (itemOpt.isPresent() && itemOpt.get().getSellerId().equals(sellerId)) {
                Item item = itemOpt.get();
                item.setQuantity(newStock);
                itemRepository.save(item);
            }
        });
    }

    @Override
    public Map<String, Object> getProductStatistics() {
        List<Item> allItems = itemRepository.findAll();
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalProducts", allItems.size());
        stats.put("totalValue", allItems.stream().mapToDouble(item -> item.getPrice().doubleValue() * item.getQuantity()).sum());
        stats.put("availableProducts", findAvailableItems().size());
        return stats;
    }

    @Override
    public List<Map<String, Object>> getCategoryStatistics() {
        List<Item> allItems = itemRepository.findAll();
        Map<String, Long> categoryCount = allItems.stream()
                .collect(Collectors.groupingBy(Item::getCategory, Collectors.counting()));
        
        return categoryCount.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> stat = new HashMap<>();
                    stat.put("category", entry.getKey());
                    stat.put("count", entry.getValue());
                    return stat;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> getTopSellingItems(int limit) {
        // Simple implementation - in production, use actual sales data
        return itemRepository.findAll().stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> getRecentlyAddedItems(int limit) {
        return itemRepository.findAll().stream()
                .sorted((a, b) -> b.getCreatedAt() != null && a.getCreatedAt() != null ? 
                    b.getCreatedAt().compareTo(a.getCreatedAt()) : 0)
                .limit(limit)
                .collect(Collectors.toList());
    }
}
