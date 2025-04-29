package com.oma.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ProductDTO {
    private UUID id;
    private String title;
    private String description;
    private double price;
    private Integer quantity;
    private boolean featured;
    private UUID storeId;
    private UUID categoryId;
}

