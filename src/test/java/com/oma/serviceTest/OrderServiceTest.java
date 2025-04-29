package com.oma.service;

import com.oma.dto.OrderDTO;
import com.oma.model.Order;
import com.oma.model.Product;
import com.oma.repository.OrderRepo;
import com.oma.repository.ProductRepo;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class OrderServiceTest {

    @Mock
    private OrderRepo orderRepo;

    @Mock
    private ProductRepo productRepo;

    @InjectMocks
    private OrderService orderService;

    @Test
    void getAllOrders_ShouldReturnOrders() {
        when(orderRepo.findAll()).thenReturn(Collections.emptyList());
        assertEquals(0, orderService.getAllOrders().size());
    }

    @Test
    void getOrderById_ShouldReturnOrder() {
        UUID id = UUID.randomUUID();
        Order order = new Order();
        when(orderRepo.findById(id)).thenReturn(Optional.of(order));

        OrderDTO found = orderService.getOrderById(id);
        assertNotNull(found);
    }

    @Test
    void getOrderByUserId_ShouldReturnOrders() {
        UUID userId = UUID.randomUUID();
        when(orderRepo.getOrderByBuyerId(userId)).thenReturn(Collections.emptyList());
        assertEquals(0, orderService.getOrderByUserId(userId).size());
    }
}
