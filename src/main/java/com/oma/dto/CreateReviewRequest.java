package com.oma.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateReviewRequest {
    private UUID productId;
    private UUID authorId;
    private int rating;
    private String comment;
}
