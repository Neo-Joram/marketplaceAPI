package com.oma.serviceTest;

import com.oma.model.*;
import com.oma.repository.*;
import com.oma.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReviewServiceTest {

    @Mock private ReviewRepo reviewRepo;
    @Mock private OrderRepo orderRepo;
    @Mock private ProductRepo productRepo;
    @Mock private UserRepo userRepo;

    @InjectMocks private ReviewService reviewService;

    private UUID testId;
    private User testUser;
    private Product testProduct;
    private Review testReview;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        testId = UUID.randomUUID();
        testUser = new User();
        testUser.setId(testId);
        testUser.setEmail("test@example.com");
        testProduct = new Product();
        testProduct.setId(testId);
        testReview = new Review();
        testReview.setId(testId);
        testReview.setRating(5);
        testReview.setComment("Great");
        testReview.setProduct(testProduct);
    }

    @Test
    public void testCreateReview_whenPurchased_success() {
        OrderItem oi = new OrderItem();
        oi.setProduct(testProduct);
        oi.setQuantity(1);
        Order order = new Order();
        order.setItemList(List.of(oi));

        when(userRepo.findByEmail("test@example.com")).thenReturn(testUser);
        when(productRepo.findById(testId)).thenReturn(Optional.of(testProduct));
        when(orderRepo.getOrderByBuyerId(testUser.getId())).thenReturn(List.of(order));
        when(reviewRepo.save(any())).thenReturn(testReview);

        Review result = reviewService.createReview(testId, "test@example.com", 5, "Excellent");
        assertEquals(5, result.getRating());
        assertEquals("Great", result.getComment());
        verify(reviewRepo).save(any());
    }

    @Test
    public void testCreateReview_whenNotPurchased_throwException() {
        when(userRepo.findByEmail("test@example.com")).thenReturn(testUser);
        when(productRepo.findById(testId)).thenReturn(Optional.of(testProduct));
        when(orderRepo.getOrderByBuyerId(testUser.getId())).thenReturn(List.of());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                reviewService.createReview(testId, "test@example.com", 4, "No buy")
        );

        assertEquals("You can only review products you purchased!", exception.getMessage());
    }

    @Test
    public void testCreateReview_whenUserNotFound_throwException() {
        when(userRepo.findByEmail("test@example.com")).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                reviewService.createReview(testId, "test@example.com", 4, "No buy")
        );

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void testCreateReview_whenProductNotFound_throwException() {
        when(userRepo.findByEmail("test@example.com")).thenReturn(testUser);
        when(productRepo.findById(testId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                reviewService.createReview(testId, "test@example.com", 4, "No buy")
        );

        assertEquals("Product not found", exception.getMessage());
    }

    @Test
    public void testGetProductReviews() {
        when(reviewRepo.findByProductId(testId)).thenReturn(List.of(testReview));

        List<Review> result = reviewService.getProductReviews(testId);
        assertEquals(1, result.size());
        verify(reviewRepo).findByProductId(testId);
    }
}
