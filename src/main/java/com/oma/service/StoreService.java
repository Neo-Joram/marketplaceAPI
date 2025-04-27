package com.oma.service;

import com.oma.model.Store;
import com.oma.repository.StoreRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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

    public Optional<Store> findById(UUID id) {
        return storeRepo.findById(id);
    }

    public Store findByOwnerId(UUID id) {return storeRepo.findByOwnerId(id);}

    @CacheEvict(value="stores", allEntries=true)
    public void createStore(Store store) {
        storeRepo.save(store);
    }

    public Store updateStore(UUID id, Store updatedStore){
        Store existingStore = storeRepo.findById(id).orElseThrow(() -> new RuntimeException("Store not found"));

        existingStore.setName(updatedStore.getName());
        existingStore.setDescription(updatedStore.getDescription());
        existingStore.setOwner(updatedStore.getOwner());
        existingStore.setProductList(updatedStore.getProductList());

        return storeRepo.save(existingStore);
    }

    public void deleteStore(UUID id) {
        storeRepo.deleteById(id);
    }
}
