package com.oma.serviceTest;

import com.oma.model.Category;
import com.oma.repository.CategoryRepo;
import com.oma.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CategoryServiceTest {

    @Mock
    private CategoryRepo categoryRepo;

    @InjectMocks
    private CategoryService categoryService;

    private Category testCategory;
    private UUID testId;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        testId = UUID.randomUUID();
        testCategory = new Category();
        testCategory.setId(testId);
        testCategory.setName("Test Category");
    }

    @Test
    void testGetAllCategories() {
        when(categoryRepo.findAll()).thenReturn(List.of(testCategory));

        List<Category> result = categoryService.getAllCategories();

        assertEquals(1, result.size());
        assertEquals("Test Category", result.get(0).getName());
    }

    @Test
    void testFindById() {
        when(categoryRepo.findById(testId)).thenReturn(java.util.Optional.of(testCategory));

        Category result = categoryService.findById(testId);

        assertEquals("Test Category", result.getName());
    }

    @Test
    void testCreateCategory() {
        when(categoryRepo.save(any(Category.class))).thenReturn(testCategory);

        Category result = categoryService.createCategory(testCategory);

        assertEquals("Test Category", result.getName());
    }

    @Test
    void testUpdateCategory() {
        when(categoryRepo.findById(testId)).thenReturn(java.util.Optional.of(testCategory));
        when(categoryRepo.save(any(Category.class))).thenReturn(testCategory);

        Category result = categoryService.updateCategory(testId, testCategory);

        assertEquals("Test Category", result.getName());
    }

    @Test
    void testDeleteCategory() {
        when(categoryRepo.existsById(testId)).thenReturn(true);
        doNothing().when(categoryRepo).deleteById(testId);

        assertDoesNotThrow(() -> categoryService.deleteCategory(testId));
    }
}
