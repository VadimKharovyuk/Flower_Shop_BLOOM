package com.example.flowershoptr.repository;

import com.example.flowershoptr.enums.BlogPostType;
import com.example.flowershoptr.model.BlogPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
    Page<BlogPost> findByType(BlogPostType type, Pageable pageable);
    Page<BlogPost> findAllByOrderByViewCountDesc(Pageable pageable);
}