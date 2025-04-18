package com.example.seller_service.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ItemDTO {

    private Long itemId;

    private String itemName;

    private Long price;

    private String description;

    private int quantity;

}