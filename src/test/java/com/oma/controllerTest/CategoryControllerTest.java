package com.oma.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oma.model.Category;
import com.oma.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CategoryControllerTest {

    @Autowired private MockMvc mockMvc;
    @Mock
    private CategoryService categoryService;
    @Autowired private ObjectMapper objectMapper;
    private UUID testId;
    private Category testCategory;
    private final String url = "/categories";

    @BeforeEach
    void setup() {
        testId = UUID.randomUUID();
        testCategory = new Category();
        testCategory.setId(testId);
        testCategory.setName("Test Category");
    }

    @Test
    void testGetAllCategories() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(List.of(testCategory));

        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Category"));
    }

    @Test
    void testGetCategoryById() throws Exception {
        when(categoryService.findById(testId)).thenReturn(testCategory);

        mockMvc.perform(get(url + "/" + testId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Category"));
    }

    @Test
    void testGetCategoryById_NotFound() throws Exception {
        when(categoryService.findById(testId)).thenThrow(new RuntimeException("Category not found"));

        mockMvc.perform(get(url + "/" + testId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateCategory() throws Exception {
        when(categoryService.createCategory(any())).thenReturn(testCategory);

        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCategory)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateCategory() throws Exception {
        when(categoryService.updateCategory(eq(testId), any())).thenReturn(testCategory);

        mockMvc.perform(put(url + "/" + testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCategory)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteCategory() throws Exception {
        doNothing().when(categoryService).deleteCategory(testId);

        mockMvc.perform(delete(url + "/" + testId))
                .andExpect(status().isNoContent());
    }
}