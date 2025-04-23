package com.example.flowershoptr.repository;

import com.example.flowershoptr.model.UserProductView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface UserProductViewRepository extends JpaRepository<UserProductView, Long> {
    List<UserProductView> findByUserEmailOrderByViewedAtDesc(String email);

    @Modifying
    @Query("DELETE FROM UserProductView upv WHERE upv.userEmail = :email AND upv.productId = :productId")
    void deleteByUserEmailAndProductId(String email, Long productId);

    @Modifying
    @Query("DELETE FROM UserProductView upv WHERE upv.userEmail = :email")
    void deleteByUserEmail(String email);

    Long countByUserEmail(String email);
}