package com.oma.repository;

import com.oma.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StoreRepo extends JpaRepository<Store, UUID> {
}
