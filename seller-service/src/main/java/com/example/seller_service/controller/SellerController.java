package com.example.seller_service.controller;


import com.example.seller_service.feign.ItemFeignClient;
import com.example.seller_service.model.ItemDTO;
import com.example.seller_service.model.Seller;
import com.example.seller_service.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/seller")
public class SellerController {

    @Autowired
    SellerService sellerService;

    @Autowired
    ItemFeignClient itemFeignClient;

    @GetMapping("/test")
    public String testEndpoint(){
        return "Test successful!";
    }

    @PostMapping
    public String addSeller(@RequestBody Seller seller){
        if(sellerService.getSellerByEmail(seller.getEmail())!=null){
            return "Seller with this email already exists, try adding another email";
        }
        Seller newSeller = sellerService.addSeller(seller);
        if(newSeller!=null){
            return "Seller added successfully!";
        }
        return "Cannot add seller";
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, String>> getSellerById(@PathVariable Long id){
        Seller seller = sellerService.getSellerById(id);
        if(seller==null){
            return new ResponseEntity<>(Map.of("message", "Seller not found!"), HttpStatus.NOT_FOUND);
        }
        Map<String,String> response = Map.of("message", "Seller found!",
                "sellerId", String.valueOf(seller.getSellerId()),
                "sellerName", seller.getSellerName(),
                "email", seller.getEmail(),
                "role", seller.getRole(),
                "token", seller.getToken());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Map<String, String>> getSellerByEmail(@PathVariable String email){
        Seller seller = sellerService.getSellerByEmail(email);
        if(seller==null){
            return new ResponseEntity<>(Map.of("message", "Seller not found!"), HttpStatus.NOT_FOUND);
        }
        Map<String,String> response = Map.of("message", "Seller found!",
                "sellerId", String.valueOf(seller.getSellerId()),
                "sellerName", seller.getSellerName(),
                "email", seller.getEmail(),
                "role", seller.getRole(),
                "token", seller.getToken());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public String deleteSeller(@PathVariable Long id){
        return sellerService.deleteSeller(id);
    }

    @GetMapping
    public List<Seller> getAllSellers(){
        return sellerService.getAllSellers();
    }

    @PostMapping("/{id}/item")
    public ResponseEntity<?> addItem(@PathVariable Long id, @RequestBody ItemDTO item){
        //check if seller exists
        Seller seller = sellerService.getSellerById(id);
        if(seller == null){
            return new ResponseEntity<>(Map.of("message" , "Seller not found"), HttpStatus.NOT_FOUND);
        }

        //add item
        itemFeignClient.addItem(item);
        Map<String,String> response = Map.of("message", "Item added successfully!",
                "itemId", String.valueOf(item.getItemId()),
                "itemName", item.getItemName(),
                "price", String.valueOf(item.getPrice()),
                "quantity", String.valueOf(item.getQuantity()));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/items")
    public ResponseEntity<?> getAllItems(){
        List<ItemDTO> list = itemFeignClient.getAllItems();
        if(list==null || list.isEmpty()){
            return new ResponseEntity<>("There are no items to display", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

}
