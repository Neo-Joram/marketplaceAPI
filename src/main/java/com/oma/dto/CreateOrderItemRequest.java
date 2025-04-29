package com.oma.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class CreateOrderItemRequest {
    private UUID productId;
    private Integer quantity;
    private double priceAtPurchase;
}
