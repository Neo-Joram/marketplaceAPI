package com.oma.repository;

import com.oma.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReviewRepo extends JpaRepository<Review, UUID> {
        List<Review> findByProductId(UUID productId);
}
