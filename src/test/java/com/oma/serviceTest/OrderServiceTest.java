package com.oma.serviceTest;

import com.oma.model.Order;
import com.oma.model.OrderStatus;
import com.oma.repository.OrderRepo;
import com.oma.repository.ProductRepo;
import com.oma.service.OrderService;
import com.oma.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    @Mock
    private OrderRepo orderRepo;

    @Mock
    private ProductRepo productRepo;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private OrderService orderService;

    private Order testOrder;
    private UUID testId;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        testId = UUID.randomUUID();
        testOrder = new Order();
        testOrder.setId(testId);
        testOrder.setStatus(OrderStatus.PENDING);
    }

    @Test
    void testGetAllOrders() {
        when(orderRepo.findAll()).thenReturn(List.of(testOrder));

        List<Order> result = orderService.getAllOrders();

        assertEquals(1, result.size());
    }

    @Test
    void testGetOrderById() {
        when(orderRepo.findById(testId)).thenReturn(java.util.Optional.of(testOrder));

        Order result = orderService.getOrderById(testId);

        assertEquals(OrderStatus.PENDING, result.getStatus());
    }

    @Test
    void testUpdateOrder() {
        when(orderRepo.findById(testId)).thenReturn(java.util.Optional.of(testOrder));
        when(orderRepo.save(any(Order.class))).thenReturn(testOrder);

        Order result = orderService.updateOrder(testId, testOrder);

        assertEquals(OrderStatus.PENDING, result.getStatus());
    }

    @Test
    void testDeleteOrder() {
        when(orderRepo.existsById(testId)).thenReturn(true);
        doNothing().when(orderRepo).deleteById(testId);

        assertDoesNotThrow(() -> orderService.deleteOrder(testId));
    }
}
