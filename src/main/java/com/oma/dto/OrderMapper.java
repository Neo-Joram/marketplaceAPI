package com.oma.dto;

import com.oma.model.Order;
import com.oma.model.OrderItem;
import com.oma.model.OrderStatus;

import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper {

    public static OrderDTO toDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setStatus(OrderStatus.valueOf(order.getStatus().name()));
        dto.setTotalPrice(order.getTotalPrice());
        dto.setCreatedAt(order.getCreatedAt());

        BuyerDTO buyerDTO = new BuyerDTO();
        buyerDTO.setId(order.getBuyer().getId());
        buyerDTO.setNames(order.getBuyer().getNames());

        dto.setBuyer(buyerDTO);

        List<OrderItemDTO> itemDTOs = order.getItemList().stream()
                .map(OrderMapper::toItemDTO)
                .collect(Collectors.toList());

        dto.setItemList(itemDTOs);

        return dto;
    }

    private static OrderItemDTO toItemDTO(OrderItem item) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setProductId(item.getProduct().getId());
        dto.setProductTitle(item.getProduct().getTitle());
        dto.setQuantity(item.getQuantity());
        dto.setPriceAtPurchase(item.getPriceAtPurchase());
        return dto;
    }
}
