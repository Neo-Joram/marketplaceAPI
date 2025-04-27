package com.oma.service;

import com.oma.model.Review;
import com.oma.repository.ReviewRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ReviewService {
    private final ReviewRepo reviewRepo;

    @Autowired
    public ReviewService(ReviewRepo reviewRepo) {
        this.reviewRepo = reviewRepo;
    }

    public Review createReview(Review review) {
        return reviewRepo.save(review);
    }

    public List<Review> getProductReviews(UUID id) {
        return reviewRepo.findByProductId(id);
    }
}
