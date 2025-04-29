package com.oma.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oma.dto.CreateOrderRequest;
import com.oma.dto.CreateOrderItemRequest;
import com.oma.model.*;
import com.oma.repository.UserRepo;
import com.oma.repository.ProductRepo;
import com.oma.repository.StoreRepo;
import com.oma.repository.CategoryRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OrderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private StoreRepo storeRepo;

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private ObjectMapper objectMapper;

    private User buyer;
    private Product product;

    @BeforeEach
    void setUp() {
        // Create dummy user
        buyer = new User();
        buyer.setNames("Test Buyer");
        buyer.setEmail("buyer@example.com");
        buyer.setPassword("password");
        buyer.setRole(Role.valueOf("ROLE_SHOPPER"));
        userRepo.save(buyer);

        // Create dummy store and category
        Store store = new Store();
        store.setName("Test Store");
        store.setDescription("Dummy store");
        store.setOwner(buyer);
        storeRepo.save(store);

        Category category = new Category();
        category.setName("Food");
        category.setSlug("food");
        categoryRepo.save(category);

        // Create dummy product
        product = new Product();
        product.setTitle("Test Product");
        product.setDescription("Dummy product");
        product.setPrice(100.0);
        product.setQuantity(10);
        product.setStore(store);
        product.setCategory(category);
        productRepo.save(product);
    }

    @Test
    @WithMockUser(roles = "SHOPPER")
    void createOrder_ShouldSucceed() throws Exception {
        // Build order request
        CreateOrderItemRequest item = new CreateOrderItemRequest();
        item.setProductId(product.getId());
        item.setQuantity(2);
        item.setPriceAtPurchase(product.getPrice());

        CreateOrderRequest orderRequest = new CreateOrderRequest();
        orderRequest.setBuyerId(buyer.getId());
        orderRequest.setItemList(Collections.singletonList(item));
        orderRequest.setStatus("PENDING");
        orderRequest.setTotalPrice(200.0);

        // Perform POST
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk());
    }
}
