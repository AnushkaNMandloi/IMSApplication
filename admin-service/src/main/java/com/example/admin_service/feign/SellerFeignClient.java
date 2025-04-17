package com.example.admin_service.feign;

import com.example.admin_service.model.SellerDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "seller-service")
public interface SellerFeignClient {

    @GetMapping("/seller")
    List<SellerDTO> getAllSellers();

}
