package com.oma.service;

import com.oma.model.Order;
import com.oma.model.OrderItem;
import com.oma.model.OrderStatus;
import com.oma.model.Product;
import com.oma.repository.OrderRepo;
import com.oma.repository.ProductRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
    private final OrderRepo orderRepo;
    private final ProductRepo productRepo;
    private final PaymentService paymentService;

    @Autowired
    public OrderService(OrderRepo orderRepo, ProductRepo productRepo, PaymentService paymentService) {
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
        this.paymentService = paymentService;
    }

    @Cacheable("orders")
    public List<Order> getAllOrders() {
        return orderRepo.findAll();
    }

    public Order getOrderById(UUID id) {
        return orderRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));
    }

    public List<Order> getOrderByUserId(UUID userId) {
        return orderRepo.getOrderByBuyerId(userId);
    }

    @Transactional
    @CacheEvict(value = "orders", allEntries = true)
    public void createOrder(Order order) throws Exception {
        if (order.getItemList() == null || order.getItemList().isEmpty()) {
            throw new RuntimeException("Order must contain at least one item.");
        }

        for (OrderItem it : order.getItemList()) {
            Product p = productRepo.findById(it.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Product not found with ID: " + it.getProduct().getId()));

            if (p.getQuantity() < it.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + p.getTitle());
            }

            order.addItem(p, it.getQuantity());
            p.setQuantity(p.getQuantity() - it.getQuantity());
            productRepo.save(p);
        }

        order.computeTotal();
        orderRepo.save(order);

        String paymentResponse = paymentService.simplePay();

        if (paymentResponse.equals("successful")) {
            order.setStatus(OrderStatus.PAID);
            orderRepo.save(order);
        } else {
            // Rollback stock
            for (OrderItem item : order.getItemList()) {
                Product product = productRepo.findById(item.getProduct().getId())
                        .orElseThrow(() -> new RuntimeException("Product not found during rollback"));
                product.setQuantity(product.getQuantity() + item.getQuantity());
                productRepo.save(product);
            }

            order.setStatus(OrderStatus.CANCELLED);
            orderRepo.save(order);

            throw new Exception("Payment failed.");
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
