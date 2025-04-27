package com.oma.service;

import com.oma.config.JwtUtil;
import com.oma.model.User;
import com.oma.model.VerificationToken;
import com.oma.repository.UserRepo;
import com.oma.repository.VerificationTokenRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepo userRepo;
    private final JwtUtil jwtUtil;
    private final VerificationTokenRepo tokenRepo;
    private final EmailService emailService;

    @Autowired
    public UserService(UserRepo userRepo, JwtUtil jwtUtil, VerificationTokenRepo tokenRepo, EmailService emailService) {
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
        this.tokenRepo = tokenRepo;
        this.emailService = emailService;
    }

    public String authenticate(String email, String password) {
        User user = userRepo.findByEmail(email);

        if (user == null || !BCrypt.checkpw(password, user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        if (!user.isEmailVerified()){
            throw new RuntimeException("Your email is not verified. Verify your email now");
        }

        return jwtUtil.generateToken(user.getEmail());
    }

    public void confirmEmail(String token){
        VerificationToken vt = tokenRepo.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid verification token"));

        if (vt.getExpiresAt().isBefore(Instant.now())) {
            throw new RuntimeException("Token expired");
        }

        User user = vt.getUser();
        user.setEmailVerified(true);
        userRepo.save(user);

        tokenRepo.delete(vt);
    }

    @Cacheable("users")
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public Optional<User> getUserById(UUID id) {
        return userRepo.findById(id);
    }

    @Transactional
    @CacheEvict(value="users", allEntries=true)
    public void createUser(User user) {
        userRepo.save(user);

        sendToken(user);
    }

    public void sendToken(User user){
        String token = UUID.randomUUID().toString();
        VerificationToken vt = new VerificationToken();
        vt.setToken(token);
        vt.setUser(user);
        vt.setExpiresAt(Instant.now().plus(Duration.ofHours(24)));
        tokenRepo.save(vt);

        emailService.sendVerificationEmail(user, token);
    }

    public User updateUser(UUID id, User updatedUser) {
        User existingUser = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        existingUser.setNames(updatedUser.getNames());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setRole(updatedUser.getRole());
        existingUser.setEmailVerified(updatedUser.isEmailVerified());
        existingUser.setCreatedAt(existingUser.getCreatedAt());

        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty() && !updatedUser.getPassword().startsWith("$2a$")) {
            existingUser.setPassword(updatedUser.getPassword());
        }

        return userRepo.save(existingUser);
    }

    public void deleteUser(UUID id) {
        userRepo.deleteById(id);
    }
}