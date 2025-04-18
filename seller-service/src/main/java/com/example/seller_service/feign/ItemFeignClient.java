package com.example.seller_service.feign;

import com.example.seller_service.model.ItemDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "item-service")
public interface ItemFeignClient {

    @PostMapping("/item")
    void addItem(@RequestBody ItemDTO itemDTO);

    @GetMapping("/item/{id}")
    ItemDTO getItemById(@PathVariable("id") Long id);

    @GetMapping("/item")
    public List<ItemDTO> getAllItems();
}
