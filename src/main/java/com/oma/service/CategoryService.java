package com.oma.service;

import com.oma.model.Category;
import com.oma.repository.CategoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CategoryService {
    private final CategoryRepo categoryRepo;

    @Autowired
    public CategoryService(CategoryRepo categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    @Cacheable("categories")
    public List<Category> getAllCategories(){
        return categoryRepo.findAll();
    }

    public Optional<Category> findById(UUID id) {
        return categoryRepo.findById(id);
    }

    @CacheEvict(value="categories", allEntries=true)
    public void createCategory(Category product) {
        categoryRepo.save(product);
    }

    public Category updateCategory(UUID id, Category newCategory) {
        Category oldCategory = categoryRepo.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));

        oldCategory.setName(newCategory.getName());
        oldCategory.setSlug(newCategory.getSlug());
        oldCategory.setProductList(newCategory.getProductList());

        return categoryRepo.save(oldCategory);
    }

    public void deleteCategory(UUID id) {
        categoryRepo.deleteById(id);
    }
}
