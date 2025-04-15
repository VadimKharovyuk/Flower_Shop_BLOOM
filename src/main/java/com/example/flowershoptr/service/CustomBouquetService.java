package com.example.flowershoptr.service;

import com.example.flowershoptr.dto.Custom.CustomBouquetRequestDTO;
import com.example.flowershoptr.enums.CustomBouquetRequestStatus;
import com.example.flowershoptr.exception.ResourceNotFoundException;
import com.example.flowershoptr.model.CustomBouquetRequest;
import com.example.flowershoptr.repository.CustomBouquetRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomBouquetService {

    private final CustomBouquetRequestRepository repository;

    @Autowired
    public CustomBouquetService(CustomBouquetRequestRepository repository) {
        this.repository = repository;
    }

    public CustomBouquetRequest saveRequest(CustomBouquetRequestDTO requestDTO) {
        CustomBouquetRequest request = new CustomBouquetRequest();
        request.setOccasion(requestDTO.getOccasion());
        request.setBudgetRange(requestDTO.getBudgetRange());
        request.setPreferences(requestDTO.getPreferences());
        request.setCustomerName(requestDTO.getCustomerName());
        request.setPhone(requestDTO.getPhone());

        return repository.save(request);
    }

    public List<CustomBouquetRequest> getAllRequests() {
        return repository.findAll();
    }

    public List<CustomBouquetRequest> getRequestsByStatus(CustomBouquetRequestStatus status) {
        return repository.findByStatus(status);
    }

    public Optional<CustomBouquetRequest> getRequestById(Long id) {
        return repository.findById(id);
    }

    public CustomBouquetRequest updateStatus(Long requestId, CustomBouquetRequestStatus newStatus, String comment) {
        CustomBouquetRequest request = repository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Заявка не найдена"));

        // Можно добавить валидацию допустимых переходов состояний
        if (!isValidStatusTransition(request.getStatus(), newStatus)) {
            throw new IllegalStateException("Недопустимый переход статуса: " +
                    request.getStatus() + " -> " + newStatus);
        }

        // Сохраняем предыдущий статус для логирования
        CustomBouquetRequestStatus previousStatus = request.getStatus();

        // Обновляем статус
        request.setStatus(newStatus);

        // Добавляем комментарий если предоставлен
        if (comment != null && !comment.isEmpty()) {
            request.setAdminComment(comment);
        }

        // Сохраняем историю изменений если необходимо
        // logStatusChange(request.getId(), previousStatus, newStatus, comment);

        return repository.save(request);
    }

    private boolean isValidStatusTransition(CustomBouquetRequestStatus current, CustomBouquetRequestStatus next) {
        // Здесь можно реализовать логику проверки допустимых переходов
        // Например:
        if (current == CustomBouquetRequestStatus.CANCELLED) {
            // Нельзя изменить статус отмененной заявки
            return false;
        }

        if (current == CustomBouquetRequestStatus.COMPLETED &&
                next != CustomBouquetRequestStatus.CANCELLED) {
            // Завершенную заявку можно только отменить
            return false;
        }

        // Другие правила...

        return true;
    }
}