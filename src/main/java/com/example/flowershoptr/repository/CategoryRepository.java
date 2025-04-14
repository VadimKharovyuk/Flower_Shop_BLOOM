package com.example.flowershoptr.repository;

import com.example.flowershoptr.dto.category.CategoryListDTO;
import com.example.flowershoptr.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {


    Page<Category> findByIsActiveTrue(Pageable pageable);

    /**
     * Найти все категории для главной страницы
     */
    Page<Category> findByIsFeaturedTrue(Pageable pageable);


    boolean existsByName(String name);

    @Query("SELECT c FROM Category c JOIN c.flowers f GROUP BY c ORDER BY SUM(f.cartAddCount) DESC")
    List<Category> getCartAddCountsByCategory(Pageable pageable);
}