package com.oma.serviceTest;

import com.oma.model.Category;
import com.oma.repository.CategoryRepo;
import com.oma.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CategoryServiceTest {

    @Mock
    private CategoryRepo categoryRepo;

    @InjectMocks
    private CategoryService categoryService;

    private UUID testId;
    private Category testCategory;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        testId = UUID.randomUUID();
        testCategory = new Category();
        testCategory.setId(testId);
        testCategory.setName("Test Category");
        testCategory.setSlug("test-category");
    }

    @Test
    public void testGetAllCategories() {
        List<Category> categories = Arrays.asList(testCategory);
        when(categoryRepo.findAll()).thenReturn(categories);

        List<Category> result = categoryService.getAllCategories();

        assertEquals(1, result.size());
        assertEquals(testCategory.getName(), result.get(0).getName());
        verify(categoryRepo, times(1)).findAll();
    }

    @Test
    public void testFindCategoryById_success() {
        when(categoryRepo.findById(testId)).thenReturn(Optional.of(testCategory));

        Optional<Category> result = categoryService.findById(testId);

        assertTrue(result.isPresent());
        assertEquals(testCategory.getName(), result.get().getName());
        verify(categoryRepo, times(1)).findById(testId);
    }

    @Test
    public void testFindCategoryById_notFound() {
        when(categoryRepo.findById(testId)).thenReturn(Optional.empty());

        Optional<Category> result = categoryService.findById(testId);

        assertFalse(result.isPresent());
    }

    @Test
    public void testCreateCategory() {
        when(categoryRepo.save(any(Category.class))).thenReturn(testCategory);

        categoryService.createCategory(testCategory);

        verify(categoryRepo, times(1)).save(testCategory);
    }

    @Test
    public void testUpdateCategory_success() {
        Category updatedCategory = new Category();
        updatedCategory.setName("Updated Category");
        updatedCategory.setSlug("updated-category");

        when(categoryRepo.findById(testId)).thenReturn(Optional.of(testCategory));
        when(categoryRepo.save(any(Category.class))).thenReturn(testCategory);

        Category result = categoryService.updateCategory(testId, updatedCategory);

        assertEquals(updatedCategory.getName(), result.getName());
        assertEquals(updatedCategory.getSlug(), result.getSlug());
        verify(categoryRepo, times(1)).findById(testId);
        verify(categoryRepo, times(1)).save(any(Category.class));
    }

    @Test
    public void testUpdateCategory_notFound() {
        when(categoryRepo.findById(testId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            categoryService.updateCategory(testId, new Category());
        });

        assertEquals("Product not found", exception.getMessage());
    }

    @Test
    public void testDeleteCategory() {
        categoryService.deleteCategory(testId);
        verify(categoryRepo, times(1)).deleteById(testId);
    }
}
