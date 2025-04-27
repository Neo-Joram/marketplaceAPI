package com.oma.repository;

import com.oma.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepo extends JpaRepository<Product, UUID> {
    Product findByStoreId(UUID id);
    Product findByCategoryId(UUID id);
}
