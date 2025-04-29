package com.oma.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oma.dto.OrderDTO;
import com.oma.model.Order;
import com.oma.model.OrderStatus;
import com.oma.service.OrderService;
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
public class OrderControllerTest {

    @Autowired private MockMvc mockMvc;
    @Mock
    private OrderService orderService;
    @Autowired private ObjectMapper objectMapper;
    private UUID testId;
    private OrderDTO testOrder;
    private final String url = "/orders";

    @BeforeEach
    void setup() {
        testId = UUID.randomUUID();
        testOrder = new OrderDTO();
        testOrder.setId(testId);
        testOrder.setStatus(OrderStatus.PENDING);
    }

    @Test
    @WithMockUser(roles = "SHOPPER")
    void testCreateOrder() throws Exception {
        doNothing().when(orderService).createOrder(any());

        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testOrder)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllOrders() throws Exception {
        when(orderService.getAllOrders()).thenReturn(List.of(testOrder));

        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    void testGetOrderById() throws Exception {
        when(orderService.getOrderById(testId)).thenReturn(testOrder);

        mockMvc.perform(get(url + "/" + testId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void testGetOrderById_NotFound() throws Exception {
        when(orderService.getOrderById(testId)).thenThrow(new RuntimeException("Order not found"));

        mockMvc.perform(get(url + "/" + testId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateOrder() throws Exception {
        when(orderService.updateOrder(eq(testId), any()));

        mockMvc.perform(put(url + "/" + testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testOrder)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteOrder() throws Exception {
        doNothing().when(orderService).deleteOrder(testId);

        mockMvc.perform(delete(url + "/" + testId))
                .andExpect(status().isOk());
    }
}