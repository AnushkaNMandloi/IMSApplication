package com.example.admin_service.feign;

import com.example.admin_service.model.ItemDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "item-service")
public interface ItemFeignClient {

    @GetMapping("/item")
    List<ItemDTO> getAllItems();

}
