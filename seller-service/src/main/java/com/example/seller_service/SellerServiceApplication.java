package com.example.seller_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SellerServiceApplication {

	public static void main(String[] args) {
		System.out.println("Hello, you are in seller service application");
		SpringApplication.run(SellerServiceApplication.class, args);
	}

}
