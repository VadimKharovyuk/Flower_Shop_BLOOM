package com.example.flowershoptr.service.serviceImpl;

import com.example.flowershoptr.dto.category.CategoryListDTO;
import com.example.flowershoptr.dto.flower.*;
import com.example.flowershoptr.maper.FlowerMapper;
import com.example.flowershoptr.model.Category;
import com.example.flowershoptr.model.Flower;
import com.example.flowershoptr.repository.CategoryRepository;
import com.example.flowershoptr.repository.FlowerRepository;
import com.example.flowershoptr.service.FlowerService;
import com.example.flowershoptr.util.CloudinaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlowerServiceImpl implements FlowerService {

    private final FlowerRepository flowerRepository;
    private final CategoryRepository categoryRepository;
    private final FlowerMapper flowerMapper;
    private final CloudinaryService cloudinaryService;

    @Override
    public Page<FlowerListDTO> getAllFlowers(Pageable pageable) {
        log.info("Получение страницы списка всех цветов. Page: {}, Size: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        return flowerRepository.findAll(pageable)
                .map(flowerMapper::toListDTO);
    }

    @Override
    public List<FlowerListDTO> getAllFlowersWithoutPagination() {
        log.info("Получение полного списка всех цветов (без пагинации)");

        return flowerRepository.findAll()
                .stream()
                .map(flowerMapper::toListDTO)
                .collect(Collectors.toList());
    }

    @Override
    public long flowerCount() {
     return   flowerRepository.count();
    }


    public Page<FlowerSearchDTO> searchFlowersFindByName(String query, Pageable pageable) {
        Page<Flower> flowerPage = flowerRepository.findByNameContainingIgnoreCase(query, pageable);
        return flowerPage.map(flowerMapper::toFlowerSearchDTO);
    }

    @Override
    public Page<FlowerSearchDTO> searchFlowersWithFilters(String query, BigDecimal minPrice, BigDecimal maxPrice, Boolean inStock, Pageable pageable) {
        // Создаем спецификацию для динамических запросов
        Specification<Flower> spec = Specification.where(null);

        // Добавляем фильтр по имени
        if (query != null && !query.isEmpty()) {
            spec = spec.and((root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + query.toLowerCase() + "%"));
        }

        // Добавляем фильтр по минимальной цене
        if (minPrice != null) {
            spec = spec.and((root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
        }

        // Добавляем фильтр по максимальной цене
        if (maxPrice != null) {
            spec = spec.and((root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
        }

        // Добавляем фильтр по наличию
        if (inStock != null && inStock) {
            spec = spec.and((root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.greaterThan(root.get("stock"), 0));
        }

        // Выполняем запрос и получаем страницу результатов
        Page<Flower> flowers = flowerRepository.findAll(spec, pageable);

        // Конвертируем результаты в DTO
        return flowers.map(flower -> {
            FlowerSearchDTO dto = new FlowerSearchDTO();
            dto.setId(flower.getId());
            dto.setName(flower.getName());
            dto.setPreviewImageUrl(flower.getPreviewImageUrl());
            dto.setPrice(flower.getPrice());
            return dto;
        });
    }

    @Override
    public Page<FlowerListDTO> getActiveFlowers(Pageable pageable) {
        log.info("Получение страницы списка активных цветов. Page: {}, Size: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        return flowerRepository.findByIsActiveTrue(pageable)
                .map(flowerMapper::toListDTO);
    }

    @Override
    public Page<FlowerListDTO> getFlowersByCategory(Long categoryId, Pageable pageable) {
        log.info("Получение страницы списка цветов по категории с ID: {}. Page: {}, Size: {}",
                categoryId, pageable.getPageNumber(), pageable.getPageSize());

        return flowerRepository.findByCategoryId(categoryId, pageable)
                .map(flowerMapper::toListDTO);
    }

    @Override
    public Page<FlowerListDTO> searchFlowersByName(String name, Pageable pageable) {
        log.info("Поиск цветов по имени: '{}'. Page: {}, Size: {}",
                name, pageable.getPageNumber(), pageable.getPageSize());

        return flowerRepository.findByNameContainingIgnoreCase(name, pageable)
                .map(flowerMapper::toListDTO);
    }

    @Override
    public FlowerDetailsDTO getFlowerById(Long id) {
        log.info("Получение информации о цветке с ID: {}", id);
        return flowerRepository.findById(id)
                .map(flowerMapper::toDetailsDTO)
                .orElseThrow(() -> new RuntimeException("Цветок с ID " + id + " не найден"));
    }

    @Override
    @Transactional
    public FlowerDetailsDTO createFlower(CreateFlowerDTO createFlowerDTO, Long categoryId) {
        log.info("Создание нового цветка в категории с ID: {}", categoryId);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Категория с ID " + categoryId + " не найдена"));

        Flower flower = flowerMapper.toEntityFromCreateDTO(createFlowerDTO);
        flower.setCategory(category);

        // Установка значений по умолчанию
        flower.setReviewCount(0);
        flower.setAverageRating(0.0);

        Flower savedFlower = flowerRepository.save(flower);
        log.info("Цветок успешно создан с ID: {}", savedFlower.getId());

        return flowerMapper.toDetailsDTO(savedFlower);
    }

    @Override
    @Transactional
    public FlowerDetailsDTO createFlowerWithImage(CreateFlowerDTO createFlowerDTO, Long categoryId, MultipartFile imageFile) {
        log.info("Создание нового цветка с изображением в категории с ID: {}", categoryId);

        try {
            // Загрузка изображения в Cloudinary
            if (imageFile != null && !imageFile.isEmpty()) {
                CloudinaryService.UploadResult uploadResult = cloudinaryService.uploadImage(imageFile);
                createFlowerDTO.setPreviewImageUrl(uploadResult.getUrl());
                createFlowerDTO.setPhotoId(extractPhotoIdFromPublicId(uploadResult.getPublicId()));

                log.info("Изображение загружено в Cloudinary. URL: {}, Public ID: {}",
                        uploadResult.getUrl(), uploadResult.getPublicId());
            }

            // Создание цветка с изображением
            return createFlower(createFlowerDTO, categoryId);

        } catch (IOException e) {
            log.error("Ошибка при загрузке изображения для цветка: {}", e.getMessage());
            throw new RuntimeException("Не удалось загрузить изображение для цветка: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public FlowerDetailsDTO updateFlower(Long id, UpdateFlowerDTO updateFlowerDTO, Long categoryId) {
        log.info("Обновление цветка с ID: {}", id);

        Flower flower = flowerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Цветок с ID " + id + " не найден"));

        flowerMapper.updateFlowerFromDTO(flower, updateFlowerDTO);

        // Обновление категории, если указана
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Категория с ID " + categoryId + " не найдена"));
            flower.setCategory(category);
        }

        Flower updatedFlower = flowerRepository.save(flower);
        log.info("Цветок с ID: {} успешно обновлен", id);

        return flowerMapper.toDetailsDTO(updatedFlower);
    }

    @Override
    @Transactional
    public FlowerDetailsDTO updateFlowerWithImage(Long id, UpdateFlowerDTO updateFlowerDTO, Long categoryId, MultipartFile imageFile) {
        log.info("Обновление цветка с ID: {} с новым изображением", id);

        Flower existingFlower = flowerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Цветок с ID " + id + " не найден"));

        try {
            // Если есть новое изображение, загружаем его и удаляем старое
            if (imageFile != null && !imageFile.isEmpty()) {
                // Удаление старого изображения, если оно существует
                if (existingFlower.getPhotoId() != null) {
                    String existingPublicId = getPublicIdFromPhotoId(existingFlower.getPhotoId());
                    boolean deleted = cloudinaryService.deleteImage(existingPublicId);
                    log.info("Удаление старого изображения: {}, успех: {}", existingPublicId, deleted);
                }

                // Загрузка нового изображения
                CloudinaryService.UploadResult uploadResult = cloudinaryService.uploadImage(imageFile);
                updateFlowerDTO.setPreviewImageUrl(uploadResult.getUrl());
                updateFlowerDTO.setPhotoId(extractPhotoIdFromPublicId(uploadResult.getPublicId()));

                log.info("Новое изображение загружено. URL: {}, Public ID: {}",
                        uploadResult.getUrl(), uploadResult.getPublicId());
            }

            // Обновление цветка с новым изображением
            return updateFlower(id, updateFlowerDTO, categoryId);

        } catch (IOException e) {
            log.error("Ошибка при обновлении изображения для цветка с ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Не удалось обновить изображение для цветка: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean deleteFlower(Long id) {
        log.info("Удаление цветка с ID: {}", id);

        Flower flower = flowerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Цветок с ID " + id + " не найден"));

        // Удаление изображения из Cloudinary, если оно существует
        if (flower.getPhotoId() != null) {
            String publicId = getPublicIdFromPhotoId(flower.getPhotoId());
            boolean imageDeleted = cloudinaryService.deleteImage(publicId);
            log.info("Удаление изображения цветка из Cloudinary. Public ID: {}, успех: {}",
                    publicId, imageDeleted);
        }

        flowerRepository.delete(flower);
        log.info("Цветок с ID: {} успешно удален", id);

        return true;
    }

    @Override
    @Transactional
    public FlowerDetailsDTO updateRating(Long id, Double rating) {
        log.info("Обновление рейтинга цветка с ID: {} на новое значение: {}", id, rating);

        Flower flower = flowerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Цветок с ID " + id + " не найден"));

        // Обновляем счетчик отзывов и средний рейтинг
        int currentReviewCount = flower.getReviewCount();
        double currentRating = flower.getAverageRating();

        // Расчет нового среднего рейтинга
        double newAverageRating;
        if (currentReviewCount == 0) {
            newAverageRating = rating;
        } else {
            // Формула: (текущий_рейтинг * кол_отзывов + новый_рейтинг) / (кол_отзывов + 1)
            newAverageRating = (currentRating * currentReviewCount + rating) / (currentReviewCount + 1);
        }

        flower.setReviewCount(currentReviewCount + 1);
        flower.setAverageRating(newAverageRating);

        Flower updatedFlower = flowerRepository.save(flower);
        log.info("Рейтинг цветка с ID: {} обновлен. Новый средний рейтинг: {}, количество отзывов: {}",
                id, updatedFlower.getAverageRating(), updatedFlower.getReviewCount());

        return flowerMapper.toDetailsDTO(updatedFlower);
    }

    @Override
    public Page<FlowerListDTO> getFlowersWithDeliveryToday(Pageable pageable) {
        log.info("Получение страницы списка цветов с доставкой сегодня. Page: {}, Size: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        return flowerRepository.findByHasDeliveryTodayTrue(pageable)
                .map(flowerMapper::toListDTO);
    }

    @Override
    @Transactional
    public FlowerDetailsDTO updateStock(Long id, Integer newCount) {
        log.info("Обновление количества на складе для цветка с ID: {} на новое значение: {}", id, newCount);

        Flower flower = flowerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Цветок с ID " + id + " не найден"));

        flower.setCount(newCount);
        Flower updatedFlower = flowerRepository.save(flower);

        log.info("Количество на складе для цветка с ID: {} обновлено на: {}", id, newCount);

        return flowerMapper.toDetailsDTO(updatedFlower);
    }

    /**
     * Извлекает ID фото из publicId Cloudinary
     */
    private Long extractPhotoIdFromPublicId(String publicId) {
        // В качестве photoId можно использовать timestamp из publicId или другой уникальный идентификатор
        // Пример реализации: преобразуем publicId в hash code для хранения в БД
        return publicId != null ? (long) publicId.hashCode() : null;
    }

    /**
     * Получает publicId Cloudinary из photoId цветка
     * Примечание: в реальном приложении нужно более надежное сопоставление,
     * возможно, хранение полного publicId в отдельном поле или в отдельной таблице
     */
    private String getPublicIdFromPhotoId(Long photoId) {
        // Это упрощенная реализация для примера
        // В реальном приложении нужно хранить соответствие между photoId и publicId
        return "flower_" + photoId;
    }
}