package com.example.user_service.feign;

import com.example.user_service.model.PurchaseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "purchase-service")
public interface PurchaseFeignClient {

    @GetMapping("/purchase/user/{userId}")
    List<PurchaseDTO> getPurchasesByUserId(@PathVariable("userId") Long userId);

    @PostMapping("/purchase")
    void createPurchase(@RequestBody PurchaseDTO purchaseDTO);
}
