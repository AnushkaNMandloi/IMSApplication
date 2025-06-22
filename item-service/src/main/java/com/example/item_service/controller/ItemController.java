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
    public ResponseEntity<Item> getItemById(@PathVariable Long id){
        Item item = itemService.getItemById(id);
        if(item==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(item, HttpStatus.OK);
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
