package com.example.flowershoptr.service;

import com.example.flowershoptr.dto.BlogPost.BlogPostCreateUpdateDto;
import com.example.flowershoptr.dto.BlogPost.BlogPostDetailsDto;
import com.example.flowershoptr.dto.BlogPost.BlogPostListDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BlogPostService {


    BlogPostDetailsDto createBlogPost(BlogPostCreateUpdateDto dto);

    BlogPostDetailsDto updateBlogPost(Long id, BlogPostCreateUpdateDto dto);

    Page<BlogPostListDto> getBlogPosts(Pageable pageable);

    BlogPostDetailsDto getBlogPostById(Long id);

    void deleteBlogPost(Long id);

    BlogPostDetailsDto incrementViewCount(Long id);
    Page<BlogPostListDto> getBlogPostsByType(String type, Pageable pageable);


    Page<BlogPostListDto> getMostPopularBlogPosts(Pageable pageable);
}