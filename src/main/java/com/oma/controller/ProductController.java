package com.oma.controller;

import com.oma.model.Product;
import com.oma.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<String> createProduct(@RequestBody Product product, Authentication authentication) {
        String email = authentication.getName();
        productService.createProduct(product, email);
        return ResponseEntity.ok("Success");
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public Optional<Product> getProductById(@PathVariable UUID id) {
        return productService.findById(id);
    }

    @GetMapping("/str/{id}")
    public Product getByStoreId(@PathVariable UUID id) {
        return productService.findByStoreId(id);
    }

    @GetMapping("/ctg/{id}")
    public Product getByCategoryId(@PathVariable UUID id) {
        return productService.findByCategoryId(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public Product updateProduct(@PathVariable UUID id, @RequestBody Product product) {
        return productService.updateProduct(id, product);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<String> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Deleted");
    }
}
