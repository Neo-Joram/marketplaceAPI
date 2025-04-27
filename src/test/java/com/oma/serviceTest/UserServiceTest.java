package com.oma.serviceTest;

import com.oma.config.JwtUtil;
import com.oma.model.*;
import com.oma.repository.*;
import com.oma.service.UserService;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.javamail.JavaMailSender;
import java.time.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock private UserRepo userRepo;
    @Mock private VerificationTokenRepo tokenRepo;
    @Mock private JavaMailSender mailSender;
    @Mock private JwtUtil jwtUtil;

    @InjectMocks private UserService userService;

    private UUID testId;
    private User testUser;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        testId = UUID.randomUUID();
        testUser = new User();
        testUser.setId(testId);
        testUser.setNames("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("$2a$10$1234567890123456789012");
        testUser.setEmailVerified(false);
    }

    @Test
    public void testGetAllUsers() {
        when(userRepo.findAll()).thenReturn(List.of(testUser));

        List<User> result = userService.getAllUsers();

        assertEquals(1, result.size());
        verify(userRepo).findAll();
    }

    @Test
    public void testAuthenticate_success() {
        when(userRepo.findByEmail("test@example.com")).thenReturn(testUser);
        when(jwtUtil.generateToken("test@example.com")).thenReturn("jwt-token");

        testUser.setEmailVerified(true);

        String token = userService.authenticate("test@example.com", "1234567890");

        assertEquals("jwt-token", token);
    }

    @Test
    public void testAuthenticate_invalidCredentials() {
        when(userRepo.findByEmail("test@example.com")).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.authenticate("test@example.com", "wrongpassword");
        });

        assertEquals("Invalid email or password", exception.getMessage());
    }

    @Test
    public void testAuthenticate_emailNotVerified() {
        when(userRepo.findByEmail("test@example.com")).thenReturn(testUser);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.authenticate("test@example.com", "1234567890");
        });

        assertEquals("Your email is not verified. Verify your email now", exception.getMessage());
    }

    @Test
    public void testConfirmEmail_success() {
        VerificationToken token = new VerificationToken();
        token.setToken("token-123");
        token.setUser(testUser);
        token.setExpiresAt(Instant.now().plus(Duration.ofHours(1)));

        when(tokenRepo.findByToken("token-123")).thenReturn(Optional.of(token));

        userService.confirmEmail("token-123");

        assertTrue(testUser.isEmailVerified());
        verify(userRepo).save(testUser);
        verify(tokenRepo).delete(token);
    }

    @Test
    public void testConfirmEmail_tokenExpired() {
        VerificationToken token = new VerificationToken();
        token.setToken("token-123");
        token.setExpiresAt(Instant.now().minus(Duration.ofHours(1)));

        when(tokenRepo.findByToken("token-123")).thenReturn(Optional.of(token));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.confirmEmail("token-123");
        });

        assertEquals("Token expired", exception.getMessage());
    }

    @Test
    public void testConfirmEmail_invalidToken() {
        when(tokenRepo.findByToken("invalid-token")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.confirmEmail("invalid-token");
        });

        assertEquals("Invalid verification token", exception.getMessage());
    }

    @Test
    public void testCreateUser() {
        doNothing().when(mailSender).send((MimeMessage) any());

        userService.createUser(testUser);

        verify(userRepo, times(1)).save(testUser);
        verify(tokenRepo, times(1)).save(any(VerificationToken.class));
    }

    @Test
    public void testUpdateUser_success() {
        when(userRepo.findById(testId)).thenReturn(Optional.of(testUser));
        when(userRepo.save(any(User.class))).thenReturn(testUser);

        User updatedUser = new User();
        updatedUser.setNames("Updated Name");
        updatedUser.setEmail("updated@example.com");

        User result = userService.updateUser(testId, updatedUser);

        assertEquals("Updated Name", result.getNames());
        assertEquals("updated@example.com", result.getEmail());
    }

    @Test
    public void testUpdateUser_notFound() {
        when(userRepo.findById(testId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUser(testId, new User());
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void testDeleteUser() {
        userService.deleteUser(testId);
        verify(userRepo).deleteById(testId);
    }
}
