package com.oma.controller;

import com.oma.dto.CreateReviewRequest;
import com.oma.model.Review;
import com.oma.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(	summary = "Add a product review (Only Shoppers)")
    public ResponseEntity<String> createReview(@RequestBody CreateReviewRequest request) {
        reviewService.createReview(request);
        return ResponseEntity.ok("Review created successfully!");
    }

    @GetMapping("/prd/{id}")
    @Operation(	summary = "Get review by product id")
    public List<Review> getByProductId(@PathVariable UUID id){
        return reviewService.getProductReviews(id);
    }
}
