package com.example.seller_service.service.impl;

import com.example.seller_service.model.Seller;
import com.example.seller_service.repository.SellerRepository;
import com.example.seller_service.service.SellerService;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SellerServiceImpl implements SellerService {

    @Autowired
    SellerRepository sellerRepository;

    @Override
    public Seller addSeller(Seller seller) {
        return sellerRepository.save(seller);
    }

    @Override
    public Seller getSellerById(Long id) {
        Optional<Seller> seller = sellerRepository.findById(id);
        return seller.orElse(null);
    }

    @Override
    public Seller getSellerByEmail(String email) {
        Optional<Seller> seller = Optional.ofNullable(sellerRepository.findByEmail(email));
        return seller.orElse(null);
    }

    @Override
    public String deleteSeller(Long id) {
        if(sellerRepository.findById(id).isPresent()){
            sellerRepository.deleteById(id);
            return "Seller deleted successfully!";
        }
        return "Seller not found";
    }

    @Override
    public List<Seller> getAllSellers() {
        return sellerRepository.findAll();
    }
}
