package com.example.flowershoptr.service;

import com.example.flowershoptr.dto.Event.EventCreateUpdateDto;
import com.example.flowershoptr.dto.Event.EventDetailsDto;
import com.example.flowershoptr.dto.Event.EventListDto;
import com.example.flowershoptr.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface EventService {
    // Методы с пагинацией
    Page<EventListDto> getAllEvents(Pageable pageable);

    Page<EventListDto> getFeaturedEvents(Pageable pageable);

    // Сохраним также методы без пагинации для случаев, когда нужны все данные
    List<EventListDto> getAllEvents();

    List<EventListDto> getFeaturedEvents(int limit);

    EventDetailsDto getEventById(Long id);

    EventDetailsDto createEvent(EventCreateUpdateDto dto, MultipartFile imageFile) throws IOException;

    EventDetailsDto updateEvent(Long id, EventCreateUpdateDto dto, MultipartFile imageFile) throws IOException;

    void deleteEvent(Long id);

    String getFormattedDay(Event event);

    String getFormattedMonth(Event event);

    String getFormattedTimeRange(Event event);

    int getRemainingSeats(Event event);

    boolean hasAvailableSeats(Event event);

    boolean deleteEventImage(Long id);
}