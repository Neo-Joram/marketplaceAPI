package com.oma.service;

import com.oma.config.JwtUtil;
import com.oma.model.User;
import com.oma.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepo userRepo;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserService(UserRepo userRepo, JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
    }

    public String authenticate(String email, String password) {
        User user = userRepo.findByEmail(email);

        if (user == null || !BCrypt.checkpw(password, user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        if (!user.isEnabled()){
            throw new RuntimeException("Your sign-in is blocked. Contact your company Administrator");
        }

        return jwtUtil.generateToken(user.getEmail());
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public Optional<User> getUserById(UUID id) {
        return userRepo.findById(id);
    }

    public void createUser(User user) {
        userRepo.save(user);
    }

    public User updateUser(UUID id, User updatedUser) {
        User existingUser = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        existingUser.setNames(updatedUser.getNames());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setRole(updatedUser.getRole());
        existingUser.setEnabled(updatedUser.isEnabled());

        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existingUser.setPassword(updatedUser.getPassword());
        }

        return userRepo.save(existingUser);
    }

    public void deleteUser(UUID id) {
        userRepo.deleteById(id);
    }
}
