package com.oma.serviceTest;

import com.oma.model.*;
import com.oma.repository.*;
import com.oma.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    @Mock private OrderRepo orderRepo;
    @Mock private ProductRepo productRepo;
    @Mock private PaymentService paymentService;

    @InjectMocks private OrderService orderService;

    private UUID testId;
    private Order testOrder;
    private Product testProduct;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        testId = UUID.randomUUID();
        testProduct = new Product();
        testProduct.setId(testId);
        testProduct.setTitle("Test Product");
        testProduct.setQuantity(5);
        testOrder = new Order();
        testOrder.setId(testId);
        testOrder.setBuyer(new User());
        testOrder.setItemList(new ArrayList<>());
    }

    @Test
    public void testGetAllOrders() {
        when(orderRepo.findAll()).thenReturn(List.of(testOrder));
        List<Order> result = orderService.getAllOrders();
        assertEquals(1, result.size());
        verify(orderRepo).findAll();
    }

    @Test
    public void testGetOrderById_success() {
        when(orderRepo.findById(testId)).thenReturn(Optional.of(testOrder));
        Optional<Order> result = orderService.getOrderById(testId);
        assertTrue(result.isPresent());
        assertEquals(testOrder.getId(), result.get().getId());
        verify(orderRepo).findById(testId);
    }

    @Test
    public void testGetOrderById_notFound() {
        when(orderRepo.findById(testId)).thenReturn(Optional.empty());
        Optional<Order> result = orderService.getOrderById(testId);
        assertFalse(result.isPresent());
    }

    @Test
    public void testCreateOrder_paymentSuccessful() throws Exception {
        OrderItem item = new OrderItem();
        item.setId(testId);
        item.setQuantity(1);
        testOrder.setItemList(List.of(item));

        when(productRepo.findById(testId)).thenReturn(Optional.of(testProduct));
        when(paymentService.simplePay()).thenReturn("successful");

        orderService.createOrder(testOrder);

        assertEquals(OrderStatus.PAID, testOrder.getStatus());
        verify(orderRepo, times(2)).save(testOrder);
    }

    @Test
    public void testCreateOrder_paymentFailed() {
        OrderItem item = new OrderItem();
        item.setId(testId);
        item.setQuantity(1);
        testOrder.setItemList(List.of(item));

        when(productRepo.findById(testId)).thenReturn(Optional.of(testProduct));
        when(paymentService.simplePay()).thenReturn("failed");

        Exception exception = assertThrows(Exception.class, () -> {
            orderService.createOrder(testOrder);
        });

        assertEquals("Payment failed.", exception.getMessage());
        assertEquals(OrderStatus.CANCELLED, testOrder.getStatus());
    }

    @Test
    public void testCreateOrder_insufficientStock() {
        testProduct.setQuantity(0);
        OrderItem item = new OrderItem();
        item.setId(testId);
        item.setQuantity(1);
        testOrder.setItemList(List.of(item));

        when(productRepo.findById(testId)).thenReturn(Optional.of(testProduct));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(testOrder);
        });

        assertEquals("Insufficient stock for product: Test Product", exception.getMessage());
    }

    @Test
    public void testUpdateOrder_success() {
        when(orderRepo.findById(testId)).thenReturn(Optional.of(testOrder));
        when(orderRepo.save(any())).thenReturn(testOrder);

        Order result = orderService.updateOrder(testId, testOrder);

        assertEquals(testOrder.getId(), result.getId());
    }

    @Test
    public void testUpdateOrder_notFound() {
        when(orderRepo.findById(testId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.updateOrder(testId, new Order());
        });

        assertEquals("Product not found", exception.getMessage());
    }

    @Test
    public void testDeleteOrder() {
        orderService.deleteOrder(testId);
        verify(orderRepo).deleteById(testId);
    }
}