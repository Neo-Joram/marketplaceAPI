package com.oma.controller;

import com.oma.model.Store;
import com.oma.repository.UserRepo;
import com.oma.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/stores")
public class StoreController {
    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    @Operation(	summary = "Create a new store (Only Sellers)")
    public ResponseEntity<String> createStore(@Valid @RequestBody Store store){
        storeService.createStore(store);
        return ResponseEntity.ok("Success");
    }

    @GetMapping
    @PreAuthorize("hasRole('SHOPPER') or hasRole('ADMIN')")
    @Operation(	summary = "Get all stores (only Admins and Shoppers)")
    public ResponseEntity<List<Store>> getAllStores(){
        return ResponseEntity.ok(storeService.getAllStores());
    }

    @GetMapping("/{id}")
    @Operation(	summary = "Get store by id")
    public Store getStoreById(@PathVariable UUID id){
        return storeService.findById(id);
    }

    @GetMapping("/own/{id}")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    @Operation(	summary = "Get store by seller id (Only Admins and Sellers)")
    public ResponseEntity<Store> getStoreByOwner(@PathVariable UUID id) {
        return ResponseEntity.ok(storeService.findByOwnerId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    @Operation(	summary = "Update store by id (Only Sellers)")
    public Store updateStore(@PathVariable UUID id, @RequestBody Store store) {
        return storeService.updateStore(id, store);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    @Operation(	summary = "Delete store by id (Only Admins and Sellers)")
    public ResponseEntity<String> deleteStore(@PathVariable UUID id) {
        storeService.deleteStore(id);
        return ResponseEntity.ok("Deleted");
    }
}
