package com.example.flowershoptr.service.serviceImpl;

import com.example.flowershoptr.dto.Event.EventCreateUpdateDto;
import com.example.flowershoptr.dto.Event.EventDetailsDto;
import com.example.flowershoptr.dto.Event.EventListDto;
import com.example.flowershoptr.maper.EventMapper;
import com.example.flowershoptr.model.Event;
import com.example.flowershoptr.repository.EventRepository;
import com.example.flowershoptr.service.EventService;
import com.example.flowershoptr.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final StorageService storageService;

    @Override
    public Page<EventListDto> getAllEvents(Pageable pageable) {
        Page<Event> eventPage = eventRepository.findAllByIsActiveTrue(pageable);
        return eventPage.map(eventMapper::toListDto);
    }

    @Override
    public Page<EventListDto> getFeaturedEvents(Pageable pageable) {
        Page<Event> eventPage = eventRepository.findByIsFeaturedTrueAndIsActiveTrue(pageable);
        return eventPage.map(eventMapper::toListDto);
    }

    @Override
    public List<EventListDto> getAllEvents() {
        List<Event> events = eventRepository.findAllByIsActiveTrue();
        return eventMapper.toListDto(events);
    }

    @Override
    public List<EventListDto> getFeaturedEvents() {
        List<Event> events = eventRepository.findByIsFeaturedTrueAndIsActiveTrue();
        return eventMapper.toListDto(events);
    }

    @Override
    public EventDetailsDto getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Событие не найдено с ID: " + id));
        return eventMapper.toDetailsDto(event);
    }

    @Override
    @Transactional
    public EventDetailsDto createEvent(EventCreateUpdateDto dto, MultipartFile imageFile) throws IOException {
        Event event = eventMapper.toEntity(dto);

        // Загрузка изображения, если оно предоставлено
        if (imageFile != null && !imageFile.isEmpty()) {
            StorageService.StorageResult result = storageService.uploadImage(imageFile);
            event.setPreviewImageUrl(result.getUrl());
            event.setImagePublicId(result.getImageId());
        }

        // Устанавливаем конечное время если его нет
        if (event.getEndTime() == null && event.getEventDate() != null && event.getDurationMinutes() != null) {
            event.setEndTime(event.getEventDate().plusMinutes(event.getDurationMinutes()));
        }

        // Логика для установки isFree на основе цены
        if (event.getPrice() == null || event.getPrice().compareTo(BigDecimal.ZERO) == 0) {
            event.setFree(true);
        }

        Event savedEvent = eventRepository.save(event);
        return eventMapper.toDetailsDto(savedEvent);
    }

    @Override
    @Transactional
    public EventDetailsDto updateEvent(Long id, EventCreateUpdateDto dto, MultipartFile imageFile) throws IOException {
        Event existingEvent = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Событие не найдено с ID: " + id));

        // Обновляем поля из DTO
        eventMapper.updateEntityFromDto(existingEvent, dto);

        // Загрузка нового изображения, если оно предоставлено
        if (imageFile != null && !imageFile.isEmpty()) {
            // Если есть старое изображение, удаляем его
            if (existingEvent.getImagePublicId() != null && !existingEvent.getImagePublicId().isEmpty()) {
                try {
                    storageService.deleteImage(existingEvent.getImagePublicId());
                } catch (Exception e) {
                    log.warn("Не удалось удалить старое изображение: {}", e.getMessage());
                    // Продолжаем выполнение даже при ошибке удаления
                }
            }

            StorageService.StorageResult result = storageService.uploadImage(imageFile);
            existingEvent.setPreviewImageUrl(result.getUrl());
            existingEvent.setImagePublicId(result.getImageId());
        }

        // Устанавливаем конечное время если его нет
        if (existingEvent.getEndTime() == null && existingEvent.getEventDate() != null && existingEvent.getDurationMinutes() != null) {
            existingEvent.setEndTime(existingEvent.getEventDate().plusMinutes(existingEvent.getDurationMinutes()));
        }

        // Логика для установки isFree на основе цены
        if (existingEvent.getPrice() == null || existingEvent.getPrice().compareTo(BigDecimal.ZERO) == 0) {
            existingEvent.setFree(true);
        } else {
            existingEvent.setFree(false);
        }

        Event updatedEvent = eventRepository.save(existingEvent);
        return eventMapper.toDetailsDto(updatedEvent);
    }


    @Transactional
    public boolean deleteEventImage(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Событие не найдено с ID: " + eventId));

        if (event.getImagePublicId() != null && !event.getImagePublicId().isEmpty()) {
            boolean deleted = storageService.deleteImage(event.getImagePublicId());
            if (deleted) {
                event.setPreviewImageUrl(null);
                event.setImagePublicId(null);
                eventRepository.save(event);
                return true;
            }
            return false;
        }
        // Изображения нет, считаем, что операция успешна
        return true;
    }

    @Override
    @Transactional
    public void deleteEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Событие не найдено с ID: " + id));

        // Можно просто изменить флаг активности
        event.setActive(false);
        eventRepository.save(event);

        // Или удалить полностью (не рекомендуется если есть связи)
        // eventRepository.delete(event);
    }

    @Override
    public String getFormattedDay(Event event) {
        return String.valueOf(event.getEventDate().getDayOfMonth());
    }

    @Override
    public String getFormattedMonth(Event event) {
        String[] monthNames = {"января", "февраля", "марта", "апреля", "мая", "июня",
                "июля", "августа", "сентября", "октября", "ноября", "декабря"};
        return monthNames[event.getEventDate().getMonthValue() - 1];
    }

    @Override
    public String getFormattedTimeRange(Event event) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String startTime = event.getEventDate().toLocalTime().format(formatter);

        String endTime;
        if (event.getEndTime() != null) {
            endTime = event.getEndTime().toLocalTime().format(formatter);
        } else if (event.getDurationMinutes() != null) {
            endTime = event.getEventDate().plusMinutes(event.getDurationMinutes()).toLocalTime().format(formatter);
        } else {
            endTime = "?";
        }

        return startTime + " - " + endTime;
    }

    @Override
    public int getRemainingSeats(Event event) {
        int bookedSeats = event.getBookings().stream()
                .filter(booking -> booking.getStatus() != com.example.flowershoptr.enums.BookingStatus.CANCELLED)
                .mapToInt(booking -> booking.getNumberOfSeats())
                .sum();

        return event.getAvailableSeats() - bookedSeats;
    }

    @Override
    public boolean hasAvailableSeats(Event event) {
        return getRemainingSeats(event) > 0;
    }
}