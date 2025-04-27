package com.oma.service;

import com.oma.model.Store;
import com.oma.repository.StoreRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class StoreService {
    private final StoreRepo storeRepo;

    @Autowired
    public StoreService(StoreRepo storeRepo) {
        this.storeRepo = storeRepo;
    }

    @Cacheable("stores")
    public List<Store> getAllStores() {
        return storeRepo.findAll();
    }

    public Store findById(UUID id) {
        return storeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Store not found with ID: " + id));
    }

    public Store findByOwnerId(UUID ownerId) {
        return storeRepo.findByOwnerId(ownerId);
    }

    @CacheEvict(value = "stores", allEntries = true)
    public Store createStore(Store store) {
        return storeRepo.save(store);
    }

    @CacheEvict(value = "stores", allEntries = true)
    public Store updateStore(UUID id, Store updatedStore) {
        Store existingStore = storeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Store not found with ID: " + id));

        existingStore.setName(updatedStore.getName());
        existingStore.setDescription(updatedStore.getDescription());
        existingStore.setOwner(updatedStore.getOwner());

        return storeRepo.save(existingStore);
    }

    @CacheEvict(value = "stores", allEntries = true)
    public void deleteStore(UUID id) {
        if (!storeRepo.existsById(id)) {
            throw new RuntimeException("Store not found with ID: " + id);
        }
        storeRepo.deleteById(id);
    }
}
