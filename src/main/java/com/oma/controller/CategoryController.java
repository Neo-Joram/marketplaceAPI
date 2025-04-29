package com.oma.controller;


import com.oma.model.Category;
import com.oma.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(	summary = "Add a new product category (Only Admins)")
    public ResponseEntity<Category> createCategory(Category category) {
        Category created = categoryService.createCategory(category);   // now returns Category
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    @GetMapping
    @Operation(	summary = "Get all categories")
    public ResponseEntity<List<Category>> getAllCategories(){
        List<Category> cats = categoryService.getAllCategories();
        return ResponseEntity.ok(cats);
    }

    @GetMapping("/{id}")
    @Operation(	summary = "Get category by id")
    public ResponseEntity<Category> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(categoryService.findById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(	summary = "Update category by id (Only Admins)")
    public ResponseEntity<Category> updateCategory(@PathVariable UUID id, @RequestBody Category category) {
        Category updated = categoryService.updateCategory(id, category);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(	summary = "Delete category by id (Only Admins)")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
