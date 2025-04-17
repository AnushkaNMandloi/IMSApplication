package com.example.purchase_service.service.impl;


import com.example.purchase_service.model.Purchase;
import com.example.purchase_service.repository.PurchaseRepository;
import com.example.purchase_service.service.PurchaseService;
import org.apache.commons.configuration.AbstractFileConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PurchaseServiceImpl implements PurchaseService {

    @Autowired
    PurchaseRepository purchaseRepository;

    @Override
    public Purchase addPurchase(Purchase purchase) {
        return purchaseRepository.save(purchase);
    }

    @Override
    public List<Purchase> getPurchaseByUserId(Long userId){
        List<Purchase> list = purchaseRepository.findByUserId(userId);
        if(list==null){
            return null;
        }
        return list;
    }

    @Override
    public List<Purchase> getAllPurchases() {
        return purchaseRepository.findAll();
    }
}
