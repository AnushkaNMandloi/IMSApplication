package com.example.item_service.service.impl;

import com.example.item_service.model.Item;
import com.example.item_service.repository.ItemRepository;
import com.example.item_service.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        Optional<Item> item = Optional.ofNullable(itemRepository.findByName(itemName));
        return item.orElse(null);
    }
}
