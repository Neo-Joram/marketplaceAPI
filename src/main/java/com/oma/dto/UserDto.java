package com.oma.dto;

import com.oma.model.Role;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserDto {
    private UUID id;
    private String names;
    private String email;
    private Role role;
    private LocalDateTime createdAt;
    private boolean emailVerified;
}
