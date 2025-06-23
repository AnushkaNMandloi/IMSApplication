package com.example.item_service.controller;

import com.example.item_service.model.Item;
import com.example.item_service.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/item")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ItemController {

    @Autowired
    ItemService itemService;

    @GetMapping("/test")
    public String testEndpoint(){
        return "Test successful!";
    }

    @PostMapping
    public String addItem(@RequestBody Item item){
        if(itemService.getItemByName(item.getItemName())!=null){
            return "Item with this name already exists, try adding another item name";
        }
        Item newItem = itemService.addItem(item);
        if(newItem!=null){
            return "Item added successfully!";
        }
        return "Cannot add item";
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getItemById(@PathVariable Long id){
        try {
            Item item = itemService.getItemById(id);
            if(item == null){
                return ResponseEntity.notFound().build();
            }
            
            // Enhanced item response for cart service
            Map<String, Object> itemResponse = Map.of(
                "id", item.getId(),
                "name", item.getItemName(),
                "description", item.getDescription() != null ? item.getDescription() : "",
                "price", item.getPrice() != null ? item.getPrice() : BigDecimal.ZERO,
                "imageUrl", item.getImageUrl() != null ? item.getImageUrl() : "",
                "sellerId", item.getSellerId() != null ? item.getSellerId() : 0L,
                "sellerName", item.getSellerName() != null ? item.getSellerName() : "Unknown Seller",
                "category", item.getCategory() != null ? item.getCategory() : "",
                "inStock", item.getQuantity() != null && item.getQuantity() > 0,
                "quantity", item.getQuantity() != null ? item.getQuantity() : 0
            );
            
            return ResponseEntity.ok(itemResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get item", "message", e.getMessage()));
        }
    }

    @GetMapping("/{id}/availability")
    public ResponseEntity<?> checkItemAvailability(@PathVariable Long id) {
        try {
            Item item = itemService.getItemById(id);
            if(item == null){
                Map<String, Object> errorResponse = Map.of("error", "Item not found", "available", false);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(errorResponse);
            }
            
            boolean isAvailable = item.getQuantity() != null && item.getQuantity() > 0;
            int availableQuantity = item.getQuantity() != null ? item.getQuantity() : 0;
            
            Map<String, Object> availability = Map.of(
                "itemId", id,
                "available", isAvailable,
                "availableQuantity", availableQuantity,
                "inStock", isAvailable
            );
            
            return ResponseEntity.ok(availability);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to check availability", "available", false));
        }
    }

    @PostMapping("/{id}/reserve")
    public ResponseEntity<?> reserveItem(@PathVariable Long id, @RequestParam int quantity) {
        try {
            Item item = itemService.getItemById(id);
            if(item == null){
                Map<String, Object> errorResponse = Map.of("error", "Item not found", "reserved", false);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(errorResponse);
            }
            
            if(item.getQuantity() == null || item.getQuantity() < quantity) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Insufficient stock", "reserved", false, 
                                   "availableQuantity", item.getQuantity() != null ? item.getQuantity() : 0));
            }
            
            // Reserve the quantity (reduce available stock)
            item.setQuantity(item.getQuantity() - quantity);
            itemService.updateItem(item);
            
            return ResponseEntity.ok(Map.of(
                "reserved", true,
                "reservedQuantity", quantity,
                "remainingQuantity", item.getQuantity()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to reserve item", "reserved", false));
        }
    }

    @PostMapping("/{id}/release")
    public ResponseEntity<?> releaseItem(@PathVariable Long id, @RequestParam int quantity) {
        try {
            Item item = itemService.getItemById(id);
            if(item == null){
                Map<String, Object> errorResponse = Map.of("error", "Item not found", "released", false);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(errorResponse);
            }
            
            // Release the quantity (increase available stock)
            item.setQuantity((item.getQuantity() != null ? item.getQuantity() : 0) + quantity);
            itemService.updateItem(item);
            
            return ResponseEntity.ok(Map.of(
                "released", true,
                "releasedQuantity", quantity,
                "totalQuantity", item.getQuantity()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to release item", "released", false));
        }
    }

    @DeleteMapping("/{id}")
    public String deleteItem(@PathVariable Long id){
        return itemService.deleteItem(id);
    }

    @GetMapping
    public List<Item> getAllItems(){
        return itemService.getAllItems();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Item>> searchItems(@RequestParam(required = false) String name,
                                                 @RequestParam(required = false) String category,
                                                 @RequestParam(required = false) Long sellerId) {
        try {
            List<Item> items = itemService.searchItems(name, category, sellerId);
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateItem(@PathVariable Long id, @RequestBody Item itemUpdate) {
        try {
            Item existingItem = itemService.getItemById(id);
            if(existingItem == null) {
                return ResponseEntity.notFound().build();
            }
            
            // Update fields
            if(itemUpdate.getItemName() != null) existingItem.setItemName(itemUpdate.getItemName());
            if(itemUpdate.getDescription() != null) existingItem.setDescription(itemUpdate.getDescription());
            if(itemUpdate.getPrice() != null) existingItem.setPrice(itemUpdate.getPrice());
            if(itemUpdate.getQuantity() != null) existingItem.setQuantity(itemUpdate.getQuantity());
            if(itemUpdate.getCategory() != null) existingItem.setCategory(itemUpdate.getCategory());
            if(itemUpdate.getImageUrl() != null) existingItem.setImageUrl(itemUpdate.getImageUrl());
            
            Item updatedItem = itemService.updateItem(existingItem);
            return ResponseEntity.ok(updatedItem);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update item", "message", e.getMessage()));
        }
    }
}
