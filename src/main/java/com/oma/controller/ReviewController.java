package com.oma.controller;

import com.oma.model.Review;
import com.oma.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    @PreAuthorize("hasRole('SHOPPER')")
    public ResponseEntity<String> createReview(Review review) {
        reviewService.createReview(review);
        return ResponseEntity.ok("Success");
    }

    @GetMapping("/prd/{id}")
    public List<Review> getByProductId(@PathVariable UUID id){
        return reviewService.getProductReviews(id);
    }
}
