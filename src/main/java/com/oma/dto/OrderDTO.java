package com.oma.dto;

import com.oma.model.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class OrderDTO {
    private UUID id;
    private BuyerDTO buyer;
    private List<OrderItemDTO> itemList;
    private OrderStatus status;
    private double totalPrice;
    private LocalDateTime createdAt;
}

