package com.example.admin_service.feign;

import com.example.admin_service.model.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "user-service")
public interface UserFeignClient {

    @GetMapping("/user")
    List<UserDTO> getAllUsers();

}
