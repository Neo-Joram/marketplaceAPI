package com.oma.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oma.dto.ProductDTO;
import com.oma.model.Product;
import com.oma.service.ProductService;
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
public class ProductControllerTest {

    @Autowired private MockMvc mockMvc;
    @Mock
    private ProductService productService;
    @Autowired private ObjectMapper objectMapper;
    private UUID testId;
    private ProductDTO testProduct;
    private final String url = "/products";

    @BeforeEach
    void setup() {
        testId = UUID.randomUUID();
        testProduct = new ProductDTO();
        testProduct.setId(testId);
        testProduct.setTitle("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(100.0);
        testProduct.setQuantity(10);
    }

    @Test
    void testGetAllProducts() throws Exception {
        when(productService.getAllProducts()).thenReturn(List.of(testProduct));

        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Product"));
    }

    @Test
    void testGetProductById() throws Exception {
        when(productService.findById(testId)).thenReturn(testProduct);

        mockMvc.perform(get(url + "/" + testId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Product"));
    }

    @Test
    void testGetProductById_NotFound() throws Exception {
        when(productService.findById(testId)).thenThrow(new RuntimeException("Product not found"));

        mockMvc.perform(get(url + "/" + testId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "SELLER")
    void testCreateProduct() throws Exception {
        when(productService.createProduct(any())).thenReturn(testProduct);

        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "SELLER")
    void testUpdateProduct() throws Exception {
        when(productService.updateProduct(eq(testId), any())).thenReturn(testProduct);

        mockMvc.perform(put(url + "/" + testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "SELLER")
    void testDeleteProduct() throws Exception {
        doNothing().when(productService).deleteProduct(testId);

        mockMvc.perform(delete(url + "/" + testId))
                .andExpect(status().isOk());
    }
}