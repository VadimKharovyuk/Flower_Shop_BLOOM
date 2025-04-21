package com.example.flowershoptr.repository;

import com.example.flowershoptr.model.Flower;
import com.example.flowershoptr.model.ProductReview;
import com.example.flowershoptr.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {
    List<ProductReview> findByUserId(Long userId);

    List<ProductReview> findByFlowerId(Long flowerId);

    boolean existsByFlowerIdAndUserId(Long flowerId, Long userId);

    boolean existsByUserAndFlower(User currentUser, Flower flower);
}
