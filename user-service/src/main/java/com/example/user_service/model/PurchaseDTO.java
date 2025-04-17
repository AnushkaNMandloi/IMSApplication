package com.example.user_service.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PurchaseDTO {
    private Long purchaseId;

    private Long userId;

    private Long itemId;

    private int quantity;
}