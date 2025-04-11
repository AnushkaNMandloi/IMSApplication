package com.example.item_service.service;

import com.example.item_service.model.Item;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ItemService {

    Item addItem(Item item);

    Item getItemById(Long id);

    String deleteItem(Long id);

    List<Item> getAllItems();

    Item getItemByName(String itemName);
}
