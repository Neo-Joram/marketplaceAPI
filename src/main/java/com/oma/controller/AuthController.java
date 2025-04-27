package com.oma.controller;

import com.oma.dto.UserDto;
import com.oma.dto.UserMapper;
import com.oma.model.User;
import com.oma.model.VerificationToken;
import com.oma.repository.UserRepo;
import com.oma.repository.VerificationTokenRepo;
import com.oma.service.EmailService;
import com.oma.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final UserRepo userRepo;
    private final VerificationTokenRepo tokenRepo;
    private final EmailService emailService;

    public AuthController(UserService userService, UserRepo userRepo, VerificationTokenRepo tokenRepo, EmailService emailService) {
        this.userService = userService;
        this.userRepo = userRepo;
        this.tokenRepo = tokenRepo;
        this.emailService = emailService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String password = request.get("password");
            String token = userService.authenticate(email, password);
            return ResponseEntity.ok(Map.of("token", token));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary="Get current user", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserDto> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepo.findByEmail(email);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(UserMapper.toDTO(user));
    }

    @PostMapping("/vrf")
    public ResponseEntity<String> sendVerificationToken(String email) {
        User user = userRepo.findByEmail(email);
        userService.sendToken(user);
        return ResponseEntity.ok("Check your email.");
    }

    @GetMapping("/confirm")
    public ResponseEntity<String> confirmEmail(@RequestParam String token) {
        userService.confirmEmail(token);
        return ResponseEntity.ok("Email verified!");
    }
}
