package com.example.seller_service.service;

import com.example.seller_service.model.Seller;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SellerService {

    Seller addSeller(Seller seller);

    Seller getSellerById(Long id);

    Seller getSellerByEmail(String email);

    String deleteSeller(Long id);

    List<Seller> getAllSellers();

}
