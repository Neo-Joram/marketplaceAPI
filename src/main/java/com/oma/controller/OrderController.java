package com.oma.controller;

import com.oma.dto.CreateOrderRequest;
import com.oma.dto.OrderDTO;
import com.oma.model.Order;
import com.oma.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @PreAuthorize("hasRole('SHOPPER')")
    @Operation(	summary = 	"Place a new order with selected products")
    public ResponseEntity<String> createOrder(@RequestBody CreateOrderRequest request) throws Exception {
        orderService.createOrder(request);
        return ResponseEntity.ok("Order created successfully!");
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(	summary = "Fetch all orders")
    public List<OrderDTO> getAllOrders(){
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    @Operation(	summary = "Get orders per order id")
    public OrderDTO getOrderById(@PathVariable UUID id) {
        return orderService.getOrderById(id);
    }

    @GetMapping("/usr/{id}")
    @PreAuthorize("hasRole('SHOPPER')")
    @Operation(	summary = "Get orders per shopper")
    public List<OrderDTO> getUserOrders(@PathVariable UUID id) {
        return orderService.getOrderByUserId(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(	summary = "Update existing order by id")
    public OrderDTO updateOrder(@PathVariable UUID id, @RequestBody OrderDTO updatedOrder) {
        throw new UnsupportedOperationException("Update not yet implemented properly with DTOs.");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(	summary = "Delete existing order")
    public ResponseEntity<String> deleteOrder(@PathVariable UUID id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok("Order deleted successfully!");
    }
}

