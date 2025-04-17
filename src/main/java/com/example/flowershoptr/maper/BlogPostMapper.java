package com.example.flowershoptr.maper;

import com.example.flowershoptr.dto.BlogPost.BlogPostCreateUpdateDto;
import com.example.flowershoptr.dto.BlogPost.BlogPostDetailsDto;
import com.example.flowershoptr.dto.BlogPost.BlogPostListDto;
import com.example.flowershoptr.model.BlogPost;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BlogPostMapper {

    // Конвертирует Entity в DetailsDTO
    public BlogPostDetailsDto toDetailsDto(BlogPost blogPost) {
        return BlogPostDetailsDto.builder()
                .id(blogPost.getId())
                .title(blogPost.getTitle())
                .shortDescription(blogPost.getShortDescription())
                .fullContent(blogPost.getFullContent())
                .previewImageUrl(blogPost.getPreviewImageUrl())
                .type(blogPost.getType())
                .viewCount(blogPost.getViewCount())
                .createdAt(blogPost.getCreatedAt())
                .updatedAt(blogPost.getUpdatedAt())
                .build();
    }

    // Конвертирует CreateUpdateDTO в Entity (для создания)
    public BlogPost toEntity(BlogPostCreateUpdateDto dto) {
        BlogPost blogPost = new BlogPost();
        blogPost.setTitle(dto.getTitle());
        blogPost.setShortDescription(dto.getShortDescription());
        blogPost.setFullContent(dto.getFullContent());
        blogPost.setPreviewImageUrl(dto.getPreviewImageUrl());
        blogPost.setImagePublicId(dto.getImagePublicId());
        blogPost.setType(dto.getType());
        return blogPost;
    }

    public void updateEntityFromDto(BlogPostCreateUpdateDto dto, BlogPost blogPost) {
        blogPost.setTitle(dto.getTitle());
        blogPost.setShortDescription(dto.getShortDescription());
        blogPost.setFullContent(dto.getFullContent());
        blogPost.setType(dto.getType());

        // Обновляем изображение только если оно предоставлено
        if (dto.getPreviewImageUrl() != null && !dto.getPreviewImageUrl().isEmpty()) {
            blogPost.setPreviewImageUrl(dto.getPreviewImageUrl());
        }

        if (dto.getImagePublicId() != null && !dto.getImagePublicId().isEmpty()) {
            blogPost.setImagePublicId(dto.getImagePublicId());
        }
    }
    public BlogPostListDto toListDto(BlogPost blogPost) {
        return BlogPostListDto.builder()
                .id(blogPost.getId())
                .title(blogPost.getTitle())
                .shortDescription(blogPost.getShortDescription())
                .previewImageUrl(blogPost.getPreviewImageUrl())
                .type(blogPost.getType())
                .viewCount(blogPost.getViewCount())
                .createdAt(blogPost.getCreatedAt())
                .build();
    }

    // Конвертирует список Entity в список ListDTO
    public List<BlogPostListDto> toListDto(List<BlogPost> blogPosts) {
        return blogPosts.stream()
                .map(this::toListDto)
                .collect(Collectors.toList());
    }
}