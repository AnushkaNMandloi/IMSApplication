package com.example.user_service.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PurchaseRequestDTO {

    private Long itemId;

    private int quantity;

}
