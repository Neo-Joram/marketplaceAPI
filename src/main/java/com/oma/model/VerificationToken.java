package com.oma.model;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
public class VerificationToken {
    @Id
    @GeneratedValue
    private UUID id;
    private String token;
    @OneToOne private User user;
    private Instant expiresAt;
}
