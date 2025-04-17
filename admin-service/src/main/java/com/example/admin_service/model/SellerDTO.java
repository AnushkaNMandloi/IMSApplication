package com.example.admin_service.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SellerDTO {

    private long sellerId;

    private String sellerName;

    private String email;

    private String role;

}
