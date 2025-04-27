package com.oma.service;

import com.oma.model.Product;
import com.oma.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {
    private final ProductRepo productRepo;

    @Autowired
    public ProductService(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    @Cacheable("products")
    public List<Product> getAllProducts(){
        return productRepo.findAll();
    }

    public Optional<Product> findById(UUID id) {
        return productRepo.findById(id);
    }

    public Product findByStoreId(UUID id) {
        return productRepo.findByStoreId(id);
    }

    public Product findByCategoryId(UUID id) {
        return productRepo.findByCategoryId(id);
    }

    @CacheEvict(value="products", allEntries=true)
    public void createProduct(Product product, String email) {
        productRepo.save(product);
    }

    public Product updateProduct(UUID id, Product newProduct) {
        Product oldProduct = productRepo.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));

        oldProduct.setTitle(newProduct.getTitle());
        oldProduct.setDescription(newProduct.getDescription());
        oldProduct.setPrice(newProduct.getPrice());
        oldProduct.setQuantity(newProduct.getQuantity());
        oldProduct.setFeatured(newProduct.isFeatured());
        oldProduct.setCategory(newProduct.getCategory());
        oldProduct.setStore(newProduct.getStore());
        oldProduct.setReview(newProduct.getReview());

        return productRepo.save(oldProduct);
    }

    public void deleteProduct(UUID id) {
        productRepo.deleteById(id);
    }
}
