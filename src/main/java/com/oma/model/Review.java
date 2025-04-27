package com.oma.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reviews")
@Data
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    private Product product;

    @ManyToOne(optional = false)
    private User author;

    private Integer rating;
    private String comment;
    private LocalDateTime createdAt = LocalDateTime.now();
}