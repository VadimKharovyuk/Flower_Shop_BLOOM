package com.example.flowershoptr.repository;

import com.example.flowershoptr.model.ShopReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopReviewRepository extends JpaRepository<ShopReview, Long> {
}
