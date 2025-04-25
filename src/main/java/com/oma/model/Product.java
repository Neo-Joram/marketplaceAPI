package com.oma.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "products")
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String title;
    private String description;
    private double price;
    private Integer quantity;
    private boolean featured;

    @ManyToOne
    private Store store;
    @ManyToOne
    private Category category;
    @OneToMany
    private List<Review> review;

    private LocalDateTime createdAt = LocalDateTime.now();
}
