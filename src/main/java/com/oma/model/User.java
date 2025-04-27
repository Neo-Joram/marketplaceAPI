package com.oma.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String names;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    private Role role;
    private String password;
    private LocalDateTime createdAt = LocalDateTime.now();
    private boolean emailVerified;

    @PrePersist
    @PreUpdate
    private void hashPassword() {
        if (password != null && !password.isEmpty()) {
            this.password = BCrypt.hashpw(password, BCrypt.gensalt());
        }
    }
}
