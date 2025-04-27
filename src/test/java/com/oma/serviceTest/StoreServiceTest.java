package com.oma.serviceTest;

import com.oma.model.*;
import com.oma.repository.*;
import com.oma.service.StoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StoreServiceTest {

    @Mock
    private StoreRepo storeRepo;

    @InjectMocks
    private StoreService storeService;

    private UUID testId;
    private Store testStore;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        testId = UUID.randomUUID();
        testStore = new Store();
        testStore.setId(testId);
        testStore.setName("Test Store");
        testStore.setDescription("Test Store Description");
    }

    @Test
    public void testGetAllStores() {
        List<Store> stores = Collections.singletonList(testStore);
        when(storeRepo.findAll()).thenReturn(stores);

        List<Store> result = storeService.getAllStores();

        assertEquals(1, result.size());
        assertEquals(testStore.getName(), result.get(0).getName());
        verify(storeRepo, times(1)).findAll();
    }

    @Test
    public void testFindStoreById_success() {
        when(storeRepo.findById(testId)).thenReturn(Optional.of(testStore));

        Optional<Store> result = storeService.findById(testId);

        assertTrue(result.isPresent());
        assertEquals(testStore.getName(), result.get().getName());
        verify(storeRepo, times(1)).findById(testId);
    }

    @Test
    public void testFindStoreById_notFound() {
        when(storeRepo.findById(testId)).thenReturn(Optional.empty());

        Optional<Store> result = storeService.findById(testId);

        assertFalse(result.isPresent());
    }

    @Test
    public void testFindStoreByOwnerId() {
        when(storeRepo.findByOwnerId(testId)).thenReturn(testStore);

        Store result = storeService.findByOwnerId(testId);

        assertEquals(testStore.getName(), result.getName());
        verify(storeRepo, times(1)).findByOwnerId(testId);
    }

    @Test
    public void testCreateStore() {
        storeService.createStore(testStore);
        verify(storeRepo, times(1)).save(testStore);
    }

    @Test
    public void testUpdateStore_success() {
        Store updatedStore = new Store();
        updatedStore.setName("Updated Store");
        updatedStore.setDescription("Updated Description");

        when(storeRepo.findById(testId)).thenReturn(Optional.of(testStore));
        when(storeRepo.save(any(Store.class))).thenReturn(testStore);

        Store result = storeService.updateStore(testId, updatedStore);

        assertEquals(updatedStore.getName(), result.getName());
        assertEquals(updatedStore.getDescription(), result.getDescription());
        verify(storeRepo, times(1)).findById(testId);
        verify(storeRepo, times(1)).save(any(Store.class));
    }

    @Test
    public void testUpdateStore_notFound() {
        when(storeRepo.findById(testId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            storeService.updateStore(testId, new Store());
        });

        assertEquals("Store not found", exception.getMessage());
    }

    @Test
    public void testDeleteStore() {
        storeService.deleteStore(testId);
        verify(storeRepo, times(1)).deleteById(testId);
    }
}
