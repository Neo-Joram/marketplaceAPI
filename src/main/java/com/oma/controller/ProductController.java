package com.oma.controller;

import com.oma.dto.ProductDTO;
import com.oma.model.Product;
import com.oma.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(	summary = "Add products to a store (only Sellers)")
    public ResponseEntity<String> createProduct(@RequestBody Product product) {
        productService.createProduct(product);
        return ResponseEntity.ok("Success");
    }

    @GetMapping
    @Operation(	summary = "Get all products")
    public List<ProductDTO> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    @Operation(	summary = "Get product by id")
    public Optional<ProductDTO> getProductById(@PathVariable UUID id) {
        return Optional.ofNullable(productService.findById(id));
    }

    @GetMapping("/str/{id}")
    @Operation(	summary = "Get products from a store by store id")
    public List<ProductDTO> getByStoreId(@PathVariable UUID id) {
        return productService.findByStoreId(id);
    }

    @GetMapping("/ctg/{id}")
    @Operation(	summary = "Get products by category id")
    public List<ProductDTO> getByCategoryId(@PathVariable UUID id) {
        return productService.findByCategoryId(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    @Operation(	summary = "Update product by id (Only Sellers)")
    public ProductDTO updateProduct(@PathVariable UUID id, @RequestBody Product product) {
        return productService.updateProduct(id, product);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    @Operation(	summary = "Delete product by id (Only Admins and Sellers)")
    public ResponseEntity<String> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Deleted");
    }
}
