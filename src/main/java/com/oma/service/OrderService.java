package com.oma.service;

import com.oma.dto.CreateOrderItemRequest;
import com.oma.dto.CreateOrderRequest;
import com.oma.dto.OrderDTO;
import com.oma.dto.OrderMapper;
import com.oma.model.*;
import com.oma.repository.OrderRepo;
import com.oma.repository.ProductRepo;
import com.oma.repository.UserRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepo orderRepo;
    private final ProductRepo productRepo;
    private final PaymentService paymentService;
    private final UserRepo userRepo;

    @Autowired
    public OrderService(OrderRepo orderRepo, ProductRepo productRepo, PaymentService paymentService, UserRepo userRepo) {
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
        this.paymentService = paymentService;
        this.userRepo = userRepo;
    }

    @Cacheable("orders")
    public List<OrderDTO> getAllOrders() {
        return orderRepo.findAll().stream()
                .map(OrderMapper::toDTO)
                .collect(Collectors.toList());
    }

    public OrderDTO getOrderById(UUID id) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return OrderMapper.toDTO(order);
    }

    public List<OrderDTO> getOrderByUserId(UUID userId) {
        return orderRepo.getOrderByBuyerId(userId).stream()
                .map(OrderMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = "orders", allEntries = true)
    public void createOrder(CreateOrderRequest request) throws Exception {
        User buyer = userRepo.findById(request.getBuyerId())
                .orElseThrow(() -> new RuntimeException("Buyer not found"));

        Order order = new Order();
        order.setBuyer(buyer);
        order.setStatus(OrderStatus.valueOf(request.getStatus()));
        order.setTotalPrice(request.getTotalPrice());

        List<OrderItem> orderItems = new ArrayList<>();
        for (CreateOrderItemRequest itemReq : request.getItemList()) {
            Product product = productRepo.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            if (product.getQuantity() < itemReq.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getTitle());
            }

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setOrder(order);
            item.setQuantity(itemReq.getQuantity());
            item.setPriceAtPurchase(product.getPrice());

            product.setQuantity(product.getQuantity() - itemReq.getQuantity());
            productRepo.save(product);

            orderItems.add(item);
        }
        order.setItemList(orderItems);
        order.computeTotal();
        orderRepo.save(order);

        String paymentResponse = paymentService.simplePay();

        if (paymentResponse.equals("successful")) {
            order.setStatus(OrderStatus.PAID);
            orderRepo.save(order);
        } else {
            rollbackStock(order.getItemList());
            order.setStatus(OrderStatus.CANCELLED);
            orderRepo.save(order);
            throw new Exception("Payment failed.");
        }
    }

    private void rollbackStock(List<OrderItem> itemList) {
        for (OrderItem item : itemList) {
            Product product = productRepo.findById(item.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Product not found during rollback"));
            product.setQuantity(product.getQuantity() + item.getQuantity());
            productRepo.save(product);
        }
    }

    @CacheEvict(value = "orders", allEntries = true)
    public Order updateOrder(UUID id, Order updatedOrder) {
        Order existingOrder = orderRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));

        existingOrder.setBuyer(updatedOrder.getBuyer());
        existingOrder.setItemList(updatedOrder.getItemList());
        existingOrder.setStatus(updatedOrder.getStatus());
        existingOrder.setTotalPrice(updatedOrder.getTotalPrice());

        return orderRepo.save(existingOrder);
    }

    @CacheEvict(value = "orders", allEntries = true)
    public void deleteOrder(UUID id) {
        if (!orderRepo.existsById(id)) {
            throw new RuntimeException("Order not found with ID: " + id);
        }
        orderRepo.deleteById(id);
    }
}
