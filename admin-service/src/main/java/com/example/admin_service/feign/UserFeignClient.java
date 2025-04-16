package com.example.admin_service.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "user-service", url="http://localhost:8080")
public class UserFeignClient {
}
