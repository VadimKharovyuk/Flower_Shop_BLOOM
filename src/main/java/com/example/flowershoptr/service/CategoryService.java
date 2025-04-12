package com.example.flowershoptr.service;

import com.example.flowershoptr.dto.category.CategoryDetailsDTO;
import com.example.flowershoptr.dto.category.CategoryListDTO;
import com.example.flowershoptr.dto.category.CategorySimpleDTO;
import com.example.flowershoptr.dto.category.CreateCategoryDTO;
import com.example.flowershoptr.dto.category.UpdateCategoryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CategoryService {

    Page<CategoryListDTO> getAllCategories(Pageable pageable);

    Page<CategoryListDTO> getActiveCategories(Pageable pageable);

    Page<CategoryListDTO> getFeaturedCategories(Pageable pageable);

    CategoryDetailsDTO getCategoryById(Long id);

    CategoryDetailsDTO updateCategory(Long id, UpdateCategoryDTO updateDTO);

    CategoryDetailsDTO toggleCategoryActive(Long id, boolean isActive);

    void deleteCategory(Long id);

    CategoryDetailsDTO createCategoryWithImage(CreateCategoryDTO createDTO, MultipartFile imageFile);

    List<CategorySimpleDTO> getAllCategoriesToAdmin();

}


