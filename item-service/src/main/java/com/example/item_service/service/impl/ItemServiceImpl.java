package com.example.item_service.service.impl;

import com.example.item_service.model.Item;
import com.example.item_service.repository.ItemRepository;
import com.example.item_service.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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
}
