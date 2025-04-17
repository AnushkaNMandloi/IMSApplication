package com.example.user_service.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PurchaseResponseDTO {

    private Long purchaseId;

    private Long itemId;

    private String itemName;

    private Long price;

    private int quantity;

}
