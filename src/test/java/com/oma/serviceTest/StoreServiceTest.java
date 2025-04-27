package com.oma.serviceTest;

import com.oma.model.Store;
import com.oma.repository.StoreRepo;
import com.oma.service.StoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StoreServiceTest {

    @Mock
    private StoreRepo storeRepo;

    @InjectMocks
    private StoreService storeService;

    private Store testStore;
    private UUID testId;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        testId = UUID.randomUUID();
        testStore = new Store();
        testStore.setId(testId);
        testStore.setName("Test Store");
    }

    @Test
    void testGetAllStores() {
        when(storeRepo.findAll()).thenReturn(List.of(testStore));

        List<Store> result = storeService.getAllStores();

        assertEquals(1, result.size());
    }

    @Test
    void testFindById() {
        when(storeRepo.findById(testId)).thenReturn(java.util.Optional.of(testStore));

        Store result = storeService.findById(testId);

        assertEquals("Test Store", result.getName());
    }

    @Test
    void testCreateStore() {
        when(storeRepo.save(any(Store.class))).thenReturn(testStore);

        Store result = storeService.createStore(testStore);

        assertEquals("Test Store", result.getName());
    }

    @Test
    void testUpdateStore() {
        when(storeRepo.findById(testId)).thenReturn(java.util.Optional.of(testStore));
        when(storeRepo.save(any(Store.class))).thenReturn(testStore);

        Store result = storeService.updateStore(testId, testStore);

        assertEquals("Test Store", result.getName());
    }

    @Test
    void testDeleteStore() {
        when(storeRepo.existsById(testId)).thenReturn(true);
        doNothing().when(storeRepo).deleteById(testId);

        assertDoesNotThrow(() -> storeService.deleteStore(testId));
    }
}
