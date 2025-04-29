package com.oma.service;

import com.oma.dto.*;
import com.oma.model.Product;
import com.oma.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService {
    private final ProductRepo productRepo;

    @Autowired
    public ProductService(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    public List<ProductDTO> getAllProducts() {
        return ProductMapper.toDTOList(productRepo.findAll());
    }

    public ProductDTO findById(UUID id) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
        return ProductMapper.toDTO(product);
    }

    public List<ProductDTO> findByStoreId(UUID storeId) {
        return ProductMapper.toDTOList(productRepo.findByStoreId(storeId));
    }

    public List<ProductDTO> findByCategoryId(UUID categoryId) {
        return ProductMapper.toDTOList(productRepo.findByCategoryId(categoryId));
    }

    @CacheEvict(value = "products", allEntries = true)
    public ProductDTO createProduct(Product product) {
        Product savedProduct = productRepo.save(product);
        return ProductMapper.toDTO(savedProduct);
    }

    @CacheEvict(value = "products", allEntries = true)
    public ProductDTO updateProduct(UUID id, Product updatedProduct) {
        Product existingProduct = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));

        existingProduct.setTitle(updatedProduct.getTitle());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setQuantity(updatedProduct.getQuantity());
        existingProduct.setFeatured(updatedProduct.isFeatured());
        existingProduct.setCategory(updatedProduct.getCategory());
        existingProduct.setStore(updatedProduct.getStore());

        Product savedProduct = productRepo.save(existingProduct);
        return ProductMapper.toDTO(savedProduct);
    }

    @CacheEvict(value = "products", allEntries = true)
    public void deleteProduct(UUID id) {
        if (!productRepo.existsById(id)) {
            throw new RuntimeException("Product not found with ID: " + id);
        }
        productRepo.deleteById(id);
    }
}
