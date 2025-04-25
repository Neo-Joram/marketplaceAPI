package com.oma.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private User buyer;
    @OneToMany
    private List<OrderItem> itemList;

    private OrderStatus status;
    private double totalPrice;
    private LocalDateTime createdAt = LocalDateTime.now();
}
