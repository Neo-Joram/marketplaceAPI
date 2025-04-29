package com.oma.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class OrderItemDTO {
    private UUID productId;
    private String productTitle;
    private Integer quantity;
    private double priceAtPurchase;
}
