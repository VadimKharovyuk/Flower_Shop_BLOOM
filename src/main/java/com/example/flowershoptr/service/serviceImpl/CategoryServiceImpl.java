package com.example.flowershoptr.service.serviceImpl;

import com.example.flowershoptr.dto.category.*;

import com.example.flowershoptr.exception.ResourceNotFoundException;
import com.example.flowershoptr.maper.CategoryMapper;
import com.example.flowershoptr.model.Category;
import com.example.flowershoptr.repository.CategoryRepository;
import com.example.flowershoptr.repository.FlowerRepository;
import com.example.flowershoptr.service.CategoryService;
import com.example.flowershoptr.util.CloudinaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final CloudinaryService cloudinaryService;


    @Override
    public Page<CategoryListDTO> getAllCategories(Pageable pageable) {
        log.info("Запрос всех категорий с пагинацией: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());
        Page<Category> categories = categoryRepository.findAll(pageable);
        return categories.map(categoryMapper::toListDTO);
    }


    @Override
    public Page<CategoryListDTO> getActiveCategories(Pageable pageable) {
        log.info("Запрос активных категорий с пагинацией: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());
        Page<Category> activeCategories = categoryRepository.findByIsActiveTrue(pageable);
        return activeCategories.map(categoryMapper::toListDTO);
    }

    @Override
    public Page<CategoryListDTO> getFeaturedCategories(Pageable pageable) {
        log.info("Запрос категорий для главной страницы с пагинацией: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());
        Page<Category> featuredCategories = categoryRepository.findByIsFeaturedTrue(pageable);
        return featuredCategories.map(categoryMapper::toListDTO);
    }

    @Override
    public CategoryDetailsDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Категория не найдена с ID: " + id));
        return categoryMapper.toDetailsDTO(category);
    }


    @Transactional
    public CategoryDetailsDTO createCategoryWithImage(CreateCategoryDTO createDTO, MultipartFile imageFile) {
        // Сначала создаем категорию
        Category category = categoryMapper.toEntityFromCreateDTO(createDTO);
        Category savedCategory = categoryRepository.save(category);
        log.info("Категория успешно создана с ID: {}", savedCategory.getId());

        // Если передан файл изображения, загружаем его
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                // Загружаем изображение
                savedCategory = uploadCategoryImage(savedCategory, imageFile);
            } catch (Exception e) {
                log.error("Ошибка при загрузке изображения для категории {}: {}",
                        savedCategory.getId(), e.getMessage());

            }
        }

        return categoryMapper.toDetailsDTO(savedCategory);
    }

    @Override
    public List<CategorySimpleDTO> getAllCategoriesToAdmin() {
        List<Category> categoryList = categoryRepository.findAll();
        return categoryList.stream()
                .map(categoryMapper::toSimpleDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryListDTO> getTotalCartAddCountByCategory(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Category> results = categoryRepository.getCartAddCountsByCategory(pageable);
        return results.stream().map(categoryMapper::toListDTO)
                .collect(Collectors.toList());

    }

    private Category uploadCategoryImage(Category category, MultipartFile imageFile) throws IOException {
        log.info("Загрузка изображения для категории ID: {}", category.getId());

        // Удаляем старое изображение, если оно есть
        if (category.getPhotoId() != null) {
            cloudinaryService.deleteImage(category.getPhotoId().toString());
        }
        CloudinaryService.UploadResult result = cloudinaryService.uploadImage(imageFile);
        category.setPreviewImageUrl(result.getUrl());
        category.setPhotoId(Long.valueOf(result.getPublicId()));

        // Сохраняем обновленную категорию и возвращаем ее
        Category updatedCategory = categoryRepository.save(category);
        log.info("Изображение для категории {} успешно загружено. URL: {}",
                category.getId(), result.getUrl());

        return updatedCategory;
    }

    @Override
    @Transactional
    public CategoryDetailsDTO updateCategory(Long id, UpdateCategoryDTO updateDTO) {
        log.info("Обновление категории с ID: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Категория не найдена с ID: " + id));

        categoryMapper.updateCategoryFromDTO(category, updateDTO);
        Category updatedCategory = categoryRepository.save(category);
        log.info("Категория успешно обновлена: {}", updatedCategory.getId());
        return categoryMapper.toDetailsDTO(updatedCategory);
    }

    @Override
    @Transactional
    public CategoryDetailsDTO toggleCategoryActive(Long id, boolean isActive) {
        log.info("Изменение статуса активности категории: id={}, isActive={}", id, isActive);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Категория не найдена с ID: " + id));

        category.setActive(isActive);
        Category savedCategory = categoryRepository.save(category);
        log.info("Статус активности категории успешно обновлен: id={}, isActive={}",
                savedCategory.getId(), savedCategory.isActive());
        return categoryMapper.toDetailsDTO(savedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        log.info("Удаление категории с ID: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Категория не найдена с ID: " + id));

        // Проверка, есть ли связанные цветы
        if (category.getFlowers() != null && !category.getFlowers().isEmpty()) {
            log.warn("Попытка удаления категории с существующими цветами: {}", id);
            throw new IllegalStateException("Невозможно удалить категорию, так как с ней связаны товары");
        }

        // Удаление изображения из Cloudinary, если оно существует
        if (category.getPhotoId() != null) {
            String publicId = category.getPhotoId().toString();
            boolean deleted = cloudinaryService.deleteImage(publicId);
            log.info("Результат удаления изображения для категории {}: {}", id, deleted);
        }

        categoryRepository.delete(category);
        log.info("Категория успешно удалена: {}", id);
    }

    /**
     * Загружает изображение для категории и обновляет её
     */
    @Transactional
    public CategoryDetailsDTO uploadCategoryImage(Long categoryId, MultipartFile file) {
        log.info("Загрузка изображения для категории с ID: {}", categoryId);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Категория не найдена с ID: " + categoryId));

        try {
            // Удаляем старое изображение, если оно есть
            if (category.getPhotoId() != null) {
                cloudinaryService.deleteImage(category.getPhotoId().toString());
            }

            // Загружаем новое изображение
            CloudinaryService.UploadResult result = cloudinaryService.uploadImage(file);
            category.setPreviewImageUrl(result.getUrl());
            category.setPhotoId(Long.valueOf(result.getPublicId()));

            Category updatedCategory = categoryRepository.save(category);
            log.info("Изображение для категории успешно обновлено: {}", categoryId);
            return categoryMapper.toDetailsDTO(updatedCategory);

        } catch (IOException e) {
            log.error("Ошибка при загрузке изображения для категории {}: {}", categoryId, e.getMessage());
            throw new RuntimeException("Ошибка при загрузке изображения: " + e.getMessage());
        }
    }
}