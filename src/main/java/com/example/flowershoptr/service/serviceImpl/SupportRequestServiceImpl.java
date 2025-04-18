package com.example.flowershoptr.service.serviceImpl;

import com.example.flowershoptr.model.SupportRequest;
import com.example.flowershoptr.repository.SupportRequestRepository;
import com.example.flowershoptr.service.SupportRequestService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SupportRequestServiceImpl implements SupportRequestService {

    private SupportRequestRepository supportRequestRepository;


    @Override
    public SupportRequest save(SupportRequest supportRequest) {
        return supportRequestRepository.save(supportRequest);
    }

    @Override
    public List<SupportRequest> findAll() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        return supportRequestRepository.findAll().stream()
                .filter(request -> request.getCreatedAt() != null && request.getCreatedAt().isAfter(sevenDaysAgo))
                .collect(Collectors.toList());
    }

    @Override
    public SupportRequest findById(Long id) {
     return supportRequestRepository.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        supportRequestRepository.deleteById(id);
    }
}
