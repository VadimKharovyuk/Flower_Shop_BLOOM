package com.example.flowershoptr.repository;

import com.example.flowershoptr.model.InstagramPost;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstagramPostRepository extends JpaRepository<InstagramPost, Long> {
    List<InstagramPost> findByActiveOrderByDisplayOrderAsc(boolean active);

    @Query("SELECT p FROM InstagramPost p WHERE p.active = true ORDER BY p.displayOrder ASC")
    List<InstagramPost> findActivePosts(Pageable pageable);
}