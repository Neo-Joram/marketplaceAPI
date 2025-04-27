package com.oma.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    public void addItem(Product product, int qty) {
        if (itemList == null) itemList = new ArrayList<>();
        OrderItem item = new OrderItem();
            item.setOrder(this);
            item.setProduct(product);
            item.setQuantity(qty);
            item.setPriceAtPurchase(product.getPrice());
            this.itemList.add(item);
    }

    // 2) Compute totalPrice as double
    public void computeTotal() {
        this.totalPrice = itemList.stream()
                .mapToDouble(i -> i.getPriceAtPurchase() * i.getQuantity())
                .sum();
    }
}
