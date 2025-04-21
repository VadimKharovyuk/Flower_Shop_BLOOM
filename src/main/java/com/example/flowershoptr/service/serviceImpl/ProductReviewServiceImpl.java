package com.example.flowershoptr.service.serviceImpl;

import com.example.flowershoptr.dto.ProductReview.CreateProductReviewDTO;
import com.example.flowershoptr.dto.ProductReview.ProductReviewDTO;
import com.example.flowershoptr.dto.ProductReview.ProductReviewSummaryDTO;
import com.example.flowershoptr.exception.UnauthorizedException;
import com.example.flowershoptr.maper.ProductReviewMapper;
import com.example.flowershoptr.model.Flower;
import com.example.flowershoptr.model.ProductReview;
import com.example.flowershoptr.model.User;
import com.example.flowershoptr.repository.FlowerRepository;
import com.example.flowershoptr.repository.OrderRepository;
import com.example.flowershoptr.repository.ProductReviewRepository;
import com.example.flowershoptr.service.AuthService;
import com.example.flowershoptr.service.ProductReviewService;
import com.example.flowershoptr.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductReviewServiceImpl implements ProductReviewService {

    private final ProductReviewRepository productReviewRepository;
    private final ProductReviewMapper productReviewMapper;
    private final FlowerRepository flowerRepository;
    private final UserService userService;
    private final AuthService authService;
    private final OrderRepository orderRepository;

    @Override
    public ProductReviewDTO createProductReview(CreateProductReviewDTO createDTO,String email) {
        // Проверка входных данных
        User currentUser = userService.getUserByEmail(email);

        if (currentUser == null) {
            throw new IllegalArgumentException("Пользователь не найден");
        }

        if (createDTO == null) {
            throw new IllegalArgumentException("Данные отзыва не могут быть null");
        }

        // Проверка существования цветка
        Flower flower = flowerRepository.findById(createDTO.getFlowerId())
                .orElseThrow(() -> new EntityNotFoundException("Цветок не найден с ID: " + createDTO.getFlowerId()));

        // Проверка наличия предыдущих отзывов от этого пользователя
        boolean hasExistingReview = productReviewRepository.existsByUserAndFlower(currentUser, flower);
        if (hasExistingReview) {
            throw new IllegalStateException("Вы уже оставляли отзыв об этом цветке");
        }

        // Создать новый отзыв
        ProductReview review = new ProductReview();
        review.setFlower(flower);
        review.setUser(currentUser);


        // Установить рейтинг с проверкой диапазона
        Integer rating = createDTO.getRating();
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Рейтинг должен быть от 1 до 5");
        }
        review.setRating(rating);

        // Установить комментарий с проверкой длины
        String comment = createDTO.getComment();
        if (comment != null) {
            comment = comment.trim(); // Удаление лишних пробелов
            if (comment.isEmpty()) {
                comment = null; // Пустой комментарий обрабатывается как null
            } else if (comment.length() > 500) {
                throw new IllegalArgumentException("Длина комментария не должна превышать 500 символов");
            }
        }
        review.setComment(comment);

        // По умолчанию устанавливаем verified как false
        review.setVerified(false);

        // Установить время создания
        review.setCreatedAt(LocalDateTime.now());

        // Сохранение в репозиторий
        ProductReview savedReview = productReviewRepository.save(review);

        // Логирование создания отзыва (опционально)
        log.info("Создан новый отзыв. ID: {}, Цветок: {}, Пользователь: {}",
                savedReview.getId(), flower.getName(), currentUser.getName());

        // Преобразование в DTO
        return productReviewMapper.toDTO(savedReview);
    }

    @Override
    public void deleteProductReview(Long reviewId, User currentUser) {
        productReviewRepository.deleteById(reviewId);


    }

    @Override
    public ProductReviewDTO getById(Long id) {
        ProductReview productReview = productReviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product review not found with id: " + id));
        return productReviewMapper.toDTO(productReview);
    }

    @Override
    public List<ProductReviewDTO> getReviewsByFlowerId(Long flowerId) {
        List<ProductReview> reviews = productReviewRepository.findByFlowerId(flowerId);

        return reviews.stream()
                .map(productReviewMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProductReviewSummaryDTO getReviewSummaryByFlowerId(Long flowerId) {
        // Проверяем существование цветка
        Flower flower = flowerRepository.findById(flowerId)
                .orElseThrow(() -> new EntityNotFoundException("Flower not found with id: " + flowerId));

        List<ProductReview> reviews = productReviewRepository.findByFlowerId(flowerId);

        ProductReviewSummaryDTO summary = new ProductReviewSummaryDTO();
        summary.setFlowerId(flowerId);

        if (reviews.isEmpty()) {
            summary.setAverageRating(0.0);
            summary.setReviewCount(0);
            summary.setRating5Count(0);
            summary.setRating4Count(0);
            summary.setRating3Count(0);
            summary.setRating2Count(0);
            summary.setRating1Count(0);
            return summary;
        }

        // Рассчитываем средний рейтинг
        double avgRating = reviews.stream()
                .mapToInt(ProductReview::getRating)
                .average()
                .orElse(0.0);

        // Округляем до одного знака после запятой
        avgRating = Math.round(avgRating * 10) / 10.0;

        // Считаем количество отзывов для каждого рейтинга
        int rating5Count = (int) reviews.stream().filter(r -> r.getRating() == 5).count();
        int rating4Count = (int) reviews.stream().filter(r -> r.getRating() == 4).count();
        int rating3Count = (int) reviews.stream().filter(r -> r.getRating() == 3).count();
        int rating2Count = (int) reviews.stream().filter(r -> r.getRating() == 2).count();
        int rating1Count = (int) reviews.stream().filter(r -> r.getRating() == 1).count();

        summary.setAverageRating(avgRating);
        summary.setReviewCount(reviews.size());
        summary.setRating5Count(rating5Count);
        summary.setRating4Count(rating4Count);
        summary.setRating3Count(rating3Count);
        summary.setRating2Count(rating2Count);
        summary.setRating1Count(rating1Count);

        return summary;
    }

    @Override
    public List<ProductReviewDTO> getUserReviews(Long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new EntityNotFoundException("User not found with id: " + userId);
        }

        List<ProductReview> userReviews = productReviewRepository.findByUserId(userId);
        return userReviews.stream()
                .map(productReviewMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<ProductReviewDTO> getCurrentUserReviews(Authentication authentication) {
        User currentUser = authService.getCurrentUser(authentication);
        if (currentUser == null) {
            throw new UnauthorizedException("User is not authenticated");
        }

        List<ProductReview> userReviews = productReviewRepository.findByUserId(currentUser.getId());
        return userReviews.stream()
                .map(productReviewMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean canUserReviewProduct(Long userId, Long flowerId) {
        // Проверяем, не оставлял ли пользователь уже отзыв
        boolean hasReviewed = productReviewRepository.existsByFlowerIdAndUserId(flowerId, userId);

        if (hasReviewed) {
            return false; // Пользователь уже оставил отзыв
        }

        // Получаем пользователя
        User user = userService.getUserById(userId);
        if (user == null) {
            return false; // Пользователь не найден
        }

        // Проверяем заказы двумя способами:
        // 1. Заказы, связанные напрямую с пользователем (когда пользователь был авторизован)
        boolean hasDirectOrders = orderRepository.existsByUserIdAndFlowerId(userId, flowerId);

        // 2. Заказы по email (для случаев, когда пользователь не был авторизован при оформлении заказа)
        boolean hasEmailOrders = orderRepository.existsByEmailAndFlowerId(user.getEmail(), flowerId);

        // Пользователь может оставить отзыв, если у него есть заказ любым из способов
        return hasDirectOrders || hasEmailOrders;
    }
}
