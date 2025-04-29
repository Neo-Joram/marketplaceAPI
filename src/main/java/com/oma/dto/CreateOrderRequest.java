package com.oma.dto;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class CreateOrderRequest {
    private UUID buyerId;
    private List<CreateOrderItemRequest> itemList;
    private String status;
    private double totalPrice;
}
