package com.oma.service;

import com.oma.model.Category;
import com.oma.repository.CategoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {
    private final CategoryRepo categoryRepo;

    @Autowired
    public CategoryService(CategoryRepo categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    @Cacheable("categories")
    public List<Category> getAllCategories() {
        return categoryRepo.findAll();
    }

    public Category findById(UUID id) {
        return categoryRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + id));
    }

    @CacheEvict(value = "categories", allEntries = true)
    public Category createCategory(Category category) {
        return categoryRepo.save(category);
    }

    @CacheEvict(value = "categories", allEntries = true)
    public Category updateCategory(UUID id, Category updatedCategory) {
        Category existingCategory = categoryRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + id));

        existingCategory.setName(updatedCategory.getName());
        existingCategory.setSlug(updatedCategory.getSlug());

        return categoryRepo.save(existingCategory);
    }

    @CacheEvict(value = "categories", allEntries = true)
    public void deleteCategory(UUID id) {
        if (!categoryRepo.existsById(id)) {
            throw new RuntimeException("Category not found with ID: " + id);
        }
        categoryRepo.deleteById(id);
    }
}
