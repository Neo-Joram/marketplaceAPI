package com.oma.repository;

import com.oma.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductRepo extends JpaRepository<Product, UUID> {
    List<Product> findByStoreId(UUID id);
    List<Product> findByCategoryId(UUID id);
}
