package com.oma.service;

import com.oma.dto.CreateReviewRequest;
import com.oma.model.Order;
import com.oma.model.Product;
import com.oma.model.Review;
import com.oma.model.User;
import com.oma.repository.OrderRepo;
import com.oma.repository.ProductRepo;
import com.oma.repository.ReviewRepo;
import com.oma.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ReviewService {
    private final ReviewRepo reviewRepo;
    private final OrderRepo orderRepo;
    private final UserRepo userRepo;
    private final ProductRepo productRepo;

    @Autowired
    public ReviewService(ReviewRepo reviewRepo, OrderRepo orderRepo, UserRepo userRepo, ProductRepo productRepo) {
        this.reviewRepo = reviewRepo;
        this.orderRepo = orderRepo;
        this.userRepo = userRepo;
        this.productRepo = productRepo;
    }

    public Review createReview(CreateReviewRequest request) {
        Product product = productRepo.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        User author = userRepo.findById(request.getAuthorId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Review review = new Review();
        review.setProduct(product);
        review.setAuthor(author);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        return reviewRepo.save(review);
    }

    public Review createReview(UUID productId, String buyerEmail, int rating, String comment) {
        User buyer = userRepo.findByEmail(buyerEmail);
        if (buyer == null) {
            throw new RuntimeException("User not found with email: " + buyerEmail);
        }

        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

        boolean hasPurchased = orderRepo.getOrderByBuyerId(buyer.getId()).stream()
                .flatMap(order -> order.getItemList().stream())
                .anyMatch(item -> item.getProduct().getId().equals(productId));

        if (!hasPurchased) {
            throw new RuntimeException("You can only review products you have purchased.");
        }

        Review review = new Review();
        review.setAuthor(buyer);
        review.setProduct(product);
        review.setRating(rating);
        review.setComment(comment);

        return reviewRepo.save(review);
    }

    public List<Review> getProductReviews(UUID productId) {
        return reviewRepo.findByProductId(productId);
    }
}
