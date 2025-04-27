package com.oma.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oma.model.Store;
import com.oma.service.StoreService;
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
public class StoreControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private StoreService storeService;
    @Autowired private ObjectMapper objectMapper;
    private UUID testId;
    private Store testStore;
    private String url = "/stores";

    @BeforeEach
    void setup() {
        testId = UUID.randomUUID();
        testStore = new Store();
        testStore.setId(testId);
        testStore.setName("Test Store");
        testStore.setDescription("Best Store in Town");
    }

    @Test
    void testGetAllStores() throws Exception {
        when(storeService.getAllStores()).thenReturn(List.of(testStore));
        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Store"));
    }

    @Test
    void testGetStoreById() throws Exception {
        when(storeService.findById(testId)).thenReturn(Optional.of(testStore));
        mockMvc.perform(get(url + "/" + testId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Store"));
    }

    @Test
    void testGetStoreById_NotFound() throws Exception {
        when(storeService.findById(testId)).thenReturn(Optional.empty());
        mockMvc.perform(get(url + "/" + testId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "SELLER")
    void testCreateStore() throws Exception {
        doNothing().when(storeService).createStore(any());
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testStore)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "SELLER")
    void testUpdateStore() throws Exception {
        when(storeService.updateStore(eq(testId), any())).thenReturn(testStore);
        mockMvc.perform(put(url + "/" + testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testStore)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "SELLER")
    void testDeleteStore() throws Exception {
        doNothing().when(storeService).deleteStore(testId);
        mockMvc.perform(delete(url + "/" + testId))
                .andExpect(status().isOk());
    }
}
