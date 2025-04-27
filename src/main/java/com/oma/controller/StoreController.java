package com.oma.controller;

import com.oma.model.Store;
import com.oma.repository.UserRepo;
import com.oma.service.StoreService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/stores")
public class StoreController {
    private final StoreService storeService;
    private final UserRepo userRepo;

    public StoreController(StoreService storeService, UserRepo userRepo) {
        this.storeService = storeService;
        this.userRepo = userRepo;
    }

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<String> createStore(@Valid @RequestBody Store store){
        storeService.createStore(store);
        return ResponseEntity.ok("Success");
    }

    @GetMapping
    public ResponseEntity<List<Store>> getAllStores(){
        return ResponseEntity.ok(storeService.getAllStores());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public Optional<Store> getStoreById(@PathVariable UUID id){
        return storeService.findById(id);
    }

    @GetMapping("/own/{id}")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<Store> getStoreByOwner(@PathVariable UUID id) {
        return ResponseEntity.ok(storeService.findByOwnerId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public Store updateStore(@PathVariable UUID id, @RequestBody Store store) {
        return storeService.updateStore(id, store);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<String> deleteStore(@PathVariable UUID id) {
        storeService.deleteStore(id);
        return ResponseEntity.ok("Deleted");
    }
}
