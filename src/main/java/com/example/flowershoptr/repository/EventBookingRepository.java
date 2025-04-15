package com.example.flowershoptr.repository;

import com.example.flowershoptr.enums.BookingStatus;
import com.example.flowershoptr.model.EventBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import java.util.List;

@Repository
public interface EventBookingRepository extends JpaRepository<EventBooking, Long> {

    /**
     * Находит все бронирования по идентификатору события
     */
    List<EventBooking> findByEventId(Long eventId);

    /**
     * Находит все бронирования по email клиента
     */
    List<EventBooking> findByCustomerEmail(String email);

    /**
     * Находит все бронирования по статусу
     */
    List<EventBooking> findByStatus(BookingStatus status);

    /**
     * Находит все бронирования по идентификатору события и статусу
     */
    List<EventBooking> findByEventIdAndStatus(Long eventId, BookingStatus status);

    /**
     * Подсчитывает количество бронирований по идентификатору события и статусу
     */
    Long countByEventIdAndStatus(Long eventId, BookingStatus status);
}