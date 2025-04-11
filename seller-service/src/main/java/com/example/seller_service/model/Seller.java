package com.example.seller_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long sellerId;

    private String sellerName;

    @Column(name = "email", unique = true)
    private String email;

    private String password;

    private String role;

    private String token;

}
