package com.example.flowershoptr.repository;

import com.example.flowershoptr.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {


    Page<Category> findByIsActiveTrue(Pageable pageable);

    /**
     * Найти все категории для главной страницы
     */
    Page<Category> findByIsFeaturedTrue(Pageable pageable);

    /**
     * Найти категорию по имени
     */
    boolean existsByName(String name);
}