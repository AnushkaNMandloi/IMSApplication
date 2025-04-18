package com.example.user_service.feign;

import com.example.user_service.model.ItemDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@FeignClient(name = "item-service")
public interface ItemFeignClient {

    @GetMapping("/item/{id}")
    ItemDTO getItemById(@PathVariable("id") Long id);

    @GetMapping("/item")
    public List<ItemDTO> getAllItems();
}
