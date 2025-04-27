package com.oma.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oma.model.User;
import com.oma.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private UserService userService;
    @Autowired private ObjectMapper objectMapper;
    private UUID testId;
    private User testUser;
    private String url = "/users";

    @BeforeEach
    void setup() {
        testId = UUID.randomUUID();
        testUser = new User();
        testUser.setId(testId);
        testUser.setNames("Test User");
        testUser.setEmail("test@example.com");
        testUser.setEmailVerified(true);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(testUser));
        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].names").value("Test User"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetUserById() throws Exception {
        when(userService.getUserById(testId)).thenReturn(Optional.of(testUser));
        mockMvc.perform(get(url + "/" + testId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.names").value("Test User"));
    }

    @Test
    void testGetUserById_NotFound() throws Exception {
        when(userService.getUserById(testId)).thenReturn(Optional.empty());
        mockMvc.perform(get(url + "/" + testId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateUser() throws Exception {
        doNothing().when(userService).createUser(any());
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testUpdateUser() throws Exception {
        when(userService.updateUser(eq(testId), any())).thenReturn(testUser);
        mockMvc.perform(put(url + "/" + testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testDeleteUser() throws Exception {
        doNothing().when(userService).deleteUser(testId);
        mockMvc.perform(delete(url + "/" + testId))
                .andExpect(status().isOk());
    }
}
