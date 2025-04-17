package com.example.purchase_service.service;

import com.example.purchase_service.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PurchaseService{

    Purchase addPurchase(Purchase purchase);

    List<Purchase> getPurchaseByUserId(Long userId);

    List<Purchase> getAllPurchases();

}
