package com.example.purchase_service.controller;


import com.example.purchase_service.model.Purchase;
import com.example.purchase_service.repository.PurchaseRepository;
import com.example.purchase_service.service.PurchaseService;
import jakarta.ws.rs.Path;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/purchase")
public class PurchaseController {

    @Autowired
    PurchaseService purchaseService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Purchase>> getPurchaseByUserId(@PathVariable Long userId){
        List<Purchase> list = purchaseService.getPurchaseByUserId(userId);
        if(list==null || list.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> createPurchase(@RequestBody Purchase purchase){
        purchaseService.addPurchase(purchase);
        return new ResponseEntity<>("Purchase saved successfully!", HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Purchase>> getAllPurchases(){
        List<Purchase> list = purchaseService.getAllPurchases();
        if(list==null || list.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

}
