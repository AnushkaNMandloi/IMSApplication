package com.example.user_service.controller;


import com.example.user_service.feign.ItemFeignClient;
import com.example.user_service.feign.PurchaseFeignClient;
import com.example.user_service.model.*;
import com.example.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    PurchaseFeignClient purchaseFeignClient;

    @Autowired
    ItemFeignClient itemFeignClient;

    @GetMapping("/test")
    public String testEndpoint(){
        return "Test successful!";
    }

    @PostMapping
    public String addUser(@RequestBody User user){
        if(userService.getUserByEmail(user.getEmail())!=null){
            return "User with this email already exists, try adding another email";
        }
        User newUser = userService.addUser(user);
        if(newUser!=null){
            return "User added successfully!";
        }
        return "Cannot add user";
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, String>> getUserById(@PathVariable Long id){
        User user = userService.getUserById(id);
        if(user==null){
            return new ResponseEntity<>(Map.of("message", "User not found!"), HttpStatus.NOT_FOUND);
        }
        Map<String,String> response = Map.of("message", "User found!",
                "userId", String.valueOf(user.getUserId()),
                "username", user.getUserName(),
                "email", user.getEmail(),
                "role", user.getRole(),
                "emailVerified", String.valueOf(user.getEmailVerified()));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Map<String, String>> getUserByEmail(@PathVariable String email){
        User user = userService.getUserByEmail(email);
        if(user==null){
            return new ResponseEntity<>(Map.of("message", "User not found!"), HttpStatus.NOT_FOUND);
        }
        Map<String,String> response = Map.of("message", "User found!",
                "userId", String.valueOf(user.getUserId()),
                "username", user.getUserName(),
                "email", user.getEmail(),
                "role", user.getRole(),
                "emailVerified", String.valueOf(user.getEmailVerified()));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id){
        return userService.deleteUser(id);
    }

    @GetMapping
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }

    @GetMapping("/items")
    public ResponseEntity<?> getAllItems(){
        List<ItemDTO> list = itemFeignClient.getAllItems();
        if(list==null || list.isEmpty()){
            return new ResponseEntity<>("There are no items to display", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping("/{id}/purchase")
    public ResponseEntity<Map<String, String>> purchaseItem(@PathVariable Long id, @RequestBody PurchaseRequestDTO request){
        //check the user first if it exists or not
        User user = userService.getUserById(id);
        if(user==null){
            return new ResponseEntity<>(Map.of("message", "User not found!"), HttpStatus.NOT_FOUND);
        }

        //verify item existance
        ItemDTO item = itemFeignClient.getItemById(request.getItemId());
        if(item==null){
            return new ResponseEntity<>(Map.of("message", "Item not found"), HttpStatus.NOT_FOUND);
        }

        PurchaseDTO purchase = new PurchaseDTO();
        purchase.setUserId(id);
        purchase.setItemId(request.getItemId());
        purchase.setQuantity(request.getQuantity());

        purchaseFeignClient.createPurchase(purchase);

        Map<String,String> response = Map.of("message", "Purchased item",
                "itemId", String.valueOf(item.getItemId()),
                "itemName", item.getItemName(),
                "price", String.valueOf(item.getPrice()),
                "quantity", String.valueOf(purchase.getQuantity()));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("{id}/purchase")
    public ResponseEntity<?> getUserPurchases(@PathVariable Long id){
        //verify user first
        User user = userService.getUserById(id);
        if(user==null){
            return new ResponseEntity<>(Map.of("message", "User not found"), HttpStatus.NOT_FOUND);
        }

        List<PurchaseDTO> list = purchaseFeignClient.getPurchasesByUserId(id);
        if(list==null || list.isEmpty()){
            return new ResponseEntity<>(Map.of("message", "No purchases found"), HttpStatus.NOT_FOUND);
        }

        List<PurchaseResponseDTO> responseList = list.stream().map(purchase -> {
            ItemDTO item = itemFeignClient.getItemById(purchase.getItemId());
            return new PurchaseResponseDTO(
                    purchase.getPurchaseId(),
                    item.getItemId(),
                    item.getItemName(),
                    item.getPrice(),
                    purchase.getQuantity()
            );
        }).toList();

        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }

}
