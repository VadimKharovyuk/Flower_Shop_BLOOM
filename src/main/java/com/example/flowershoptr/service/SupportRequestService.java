package com.example.flowershoptr.service;

import com.example.flowershoptr.model.SupportRequest;

import java.util.List;

public interface SupportRequestService {
    SupportRequest save(SupportRequest supportRequest);
    List<SupportRequest> findAll();
    SupportRequest findById(Long id);
    void delete(Long id);
}
