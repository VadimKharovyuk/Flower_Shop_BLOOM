package com.example.flowershoptr.repository;

import com.example.flowershoptr.enums.CustomBouquetRequestStatus;
import com.example.flowershoptr.model.CustomBouquetRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomBouquetRequestRepository extends JpaRepository<CustomBouquetRequest, Long> {
    List<CustomBouquetRequest> findByStatus(CustomBouquetRequestStatus status);
    List<CustomBouquetRequest> findByCustomerNameContainingIgnoreCase(String name);
    List<CustomBouquetRequest> findByPhoneContaining(String phone);
}