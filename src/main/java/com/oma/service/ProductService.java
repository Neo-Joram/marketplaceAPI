package com.oma.service;

import com.oma.model.Product;
import com.oma.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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

    @Cacheable("products")
    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    public Product findById(UUID id) {
        return productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
    }

    public List<Product> findByStoreId(UUID storeId) {
        return productRepo.findByStoreId(storeId);
    }

    public List<Product> findByCategoryId(UUID categoryId) {
        return productRepo.findByCategoryId(categoryId);
    }

    @CacheEvict(value = "products", allEntries = true)
    public Product createProduct(Product product) {
        return productRepo.save(product);
    }

    @CacheEvict(value = "products", allEntries = true)
    public Product updateProduct(UUID id, Product updatedProduct) {
        Product existingProduct = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));

        existingProduct.setTitle(updatedProduct.getTitle());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setQuantity(updatedProduct.getQuantity());
        existingProduct.setFeatured(updatedProduct.isFeatured());
        existingProduct.setCategory(updatedProduct.getCategory());
        existingProduct.setStore(updatedProduct.getStore());

        return productRepo.save(existingProduct);
    }

    @CacheEvict(value = "products", allEntries = true)
    public void deleteProduct(UUID id) {
        if (!productRepo.existsById(id)) {
            throw new RuntimeException("Product not found with ID: " + id);
        }
        productRepo.deleteById(id);
    }
}
