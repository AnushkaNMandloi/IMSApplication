package com.example.admin_service.controller;

import com.example.admin_service.feign.ItemFeignClient;
import com.example.admin_service.feign.SellerFeignClient;
import com.example.admin_service.feign.UserFeignClient;
import com.example.admin_service.model.ItemDTO;
import com.example.admin_service.model.SellerDTO;
import com.example.admin_service.model.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    UserFeignClient userFeignClient;

    @Autowired
    SellerFeignClient sellerFeignClient;

    @Autowired
    ItemFeignClient itemFeignClient;

    @GetMapping
    public String test(){
        return "Hello world!";
    }

    @GetMapping("/user")
    public List<UserDTO> getAllUsers(){
        return userFeignClient.getAllUsers();
    }

    @GetMapping("/seller")
    public List<SellerDTO> getAllSellers(){
        return sellerFeignClient.getAllSellers();
    }

    @GetMapping("/item")
    public List<ItemDTO> getAllItems(){
        return itemFeignClient.getAllItems();
    }

}
