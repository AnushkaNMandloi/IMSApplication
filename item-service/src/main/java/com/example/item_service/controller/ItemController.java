package com.example.item_service.controller;


import com.example.item_service.model.Item;
import com.example.item_service.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/item")
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
    public ResponseEntity<Map<String, String>> getItemById(@PathVariable Long id){
        Item item = itemService.getItemById(id);
        if(item==null){
            return new ResponseEntity<>(Map.of("message", "Item not found!"), HttpStatus.NOT_FOUND);
        }
        Map<String,String> response = Map.of("message", "Item found!",
                "itemName", item.getItemName(),
                "price", String.valueOf(item.getPrice()),
                "description", item.getDescription(),
                "quantity", String.valueOf(item.getQuantity()));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public String deleteItem(@PathVariable Long id){
        return itemService.deleteItem(id);
    }

    @GetMapping
    public List<Item> getAllItems(){
        return itemService.getAllItems();
    }


}
