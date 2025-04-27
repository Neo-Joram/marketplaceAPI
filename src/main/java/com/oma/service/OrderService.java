package com.oma.service;

import com.oma.model.Order;
import com.oma.model.OrderItem;
import com.oma.model.Product;
import com.oma.repository.OrderRepo;
import com.oma.repository.ProductRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {
    private final OrderRepo orderRepo;
    private final ProductRepo productRepo;

    @Autowired
    public OrderService(OrderRepo orderRepo, ProductRepo productRepo) {
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
    }

    @Cacheable("orders")
    public List<Order> getAllOrders(){return orderRepo.findAll();}

    public Optional<Order> getOrderById(UUID id) {
        return orderRepo.findById(id);
    }

    public List<Order> getOrderByUserId(UUID id) {
        return orderRepo.getOrderByBuyerId(id);
    }

    @Transactional
    @CacheEvict(value="orders", allEntries=true)
    public void createOrder(Order order) {
        for (OrderItem it : order.getItemList()) {
            Product p = productRepo.findById(it.getId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            order.addItem(p, it.getQuantity());
        }

        order.computeTotal();

        orderRepo.save(order);
    }

    public Order updateOrder(UUID id, Order updatedOrder) {
        Order existingOrder = orderRepo.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));

        existingOrder.setBuyer(updatedOrder.getBuyer());
        existingOrder.setItemList(updatedOrder.getItemList());
        existingOrder.setStatus(updatedOrder.getStatus());
        existingOrder.setTotalPrice(updatedOrder.getTotalPrice());

        return orderRepo.save(existingOrder);
    }

    public void deleteOrder(UUID id){
        orderRepo.deleteById(id);
    }
}
