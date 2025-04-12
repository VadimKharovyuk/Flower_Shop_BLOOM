package com.example.flowershoptr.service;

import com.example.flowershoptr.dto.category.CategoryDetailsDTO;
import com.example.flowershoptr.dto.category.CategoryListDTO;
import com.example.flowershoptr.dto.category.CategorySimpleDTO;
import com.example.flowershoptr.dto.category.CreateCategoryDTO;
import com.example.flowershoptr.dto.category.UpdateCategoryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {



    Page<CategoryListDTO> getAllCategories(Pageable pageable);

    /**
     * Получить страницу активных категорий
     * @param pageable параметры пагинации
     */
    Page<CategoryListDTO> getActiveCategories(Pageable pageable);

   //которые MainPage
    Page<CategoryListDTO> getFeaturedCategories(Pageable pageable);



    CategoryDetailsDTO getCategoryById(Long id);


    CategoryDetailsDTO createCategory(CreateCategoryDTO createDTO);


    CategoryDetailsDTO updateCategory(Long id, UpdateCategoryDTO updateDTO);

    /**
     * Активировать/деактивировать категорию
     */
    CategoryDetailsDTO toggleCategoryActive(Long id, boolean isActive);


    void deleteCategory(Long id);


}