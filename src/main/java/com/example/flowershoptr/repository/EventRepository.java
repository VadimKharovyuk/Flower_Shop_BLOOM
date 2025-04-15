package com.example.flowershoptr.repository;

import com.example.flowershoptr.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByIsActiveTrue();

    List<Event> findByIsFeaturedTrueAndIsActiveTrue();

    // Методы с пагинацией
    Page<Event> findAllByIsActiveTrue(Pageable pageable);

    Page<Event> findByIsFeaturedTrueAndIsActiveTrue(Pageable pageable);

    List<Event> findByIsFeaturedTrueAndIsActiveTrueOrderByEventDateDesc(Pageable pageable);

}