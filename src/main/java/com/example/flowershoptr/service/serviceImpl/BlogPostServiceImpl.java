package com.example.flowershoptr.service.serviceImpl;
import com.example.flowershoptr.dto.BlogPost.BlogPostCreateUpdateDto;
import com.example.flowershoptr.dto.BlogPost.BlogPostDetailsDto;
import com.example.flowershoptr.dto.BlogPost.BlogPostListDto;
import com.example.flowershoptr.enums.BlogPostType;
import com.example.flowershoptr.maper.BlogPostMapper;
import com.example.flowershoptr.model.BlogPost;
import com.example.flowershoptr.repository.BlogPostRepository;
import com.example.flowershoptr.service.BlogPostService;
import com.example.flowershoptr.service.StorageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class BlogPostServiceImpl implements BlogPostService {

    private final BlogPostRepository blogPostRepository;
    private final BlogPostMapper blogPostMapper;
    private final StorageService storageService;

    @Override
    @Transactional
    public BlogPostDetailsDto createBlogPost(BlogPostCreateUpdateDto dto) {
        BlogPost blogPost = blogPostMapper.toEntity(dto);
        blogPost = blogPostRepository.save(blogPost);
        return blogPostMapper.toDetailsDto(blogPost);
    }

    @Transactional
    public BlogPostDetailsDto createBlogPostWithImage(BlogPostCreateUpdateDto dto, MultipartFile image) throws IOException {
        if (image != null && !image.isEmpty()) {
            StorageService.StorageResult result = storageService.uploadImage(image);
            dto = BlogPostCreateUpdateDto.builder()
                    .title(dto.getTitle())
                    .shortDescription(dto.getShortDescription())
                    .fullContent(dto.getFullContent())
                    .type(dto.getType())
                    .previewImageUrl(result.getUrl())
                    .imagePublicId(result.getImageId())
                    .build();
        }

        return createBlogPost(dto);
    }

    @Override
    @Transactional
    public BlogPostDetailsDto updateBlogPost(Long id, BlogPostCreateUpdateDto dto) {
        BlogPost blogPost = blogPostRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Blog post not found with id: " + id));

        blogPostMapper.updateEntityFromDto(dto, blogPost);
        blogPost = blogPostRepository.save(blogPost);
        return blogPostMapper.toDetailsDto(blogPost);
    }

    @Transactional
    public BlogPostDetailsDto updateBlogPostWithImage(Long id, BlogPostCreateUpdateDto dto, MultipartFile image) throws IOException {
        BlogPost blogPost = blogPostRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Blog post not found with id: " + id));

        if (image != null && !image.isEmpty()) {
            // Удаляем старое изображение, если оно есть
            if (blogPost.getImagePublicId() != null) {
                storageService.deleteImage(blogPost.getImagePublicId());
            }
            // Загружаем новое изображение
            StorageService.StorageResult result = storageService.uploadImage(image);
            dto = BlogPostCreateUpdateDto.builder()
                    .title(dto.getTitle())
                    .shortDescription(dto.getShortDescription())
                    .fullContent(dto.getFullContent())
                    .type(dto.getType())
                    .previewImageUrl(result.getUrl())
                    .imagePublicId(result.getImageId())
                    .build();
        }

        return updateBlogPost(id, dto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BlogPostListDto> getBlogPosts(Pageable pageable) {
        return blogPostRepository.findAll(pageable)
                .map(blogPostMapper::toListDto);
    }

    @Override
    @Transactional(readOnly = true)
    public BlogPostDetailsDto getBlogPostById(Long id) {
        BlogPost blogPost = blogPostRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Blog post not found with id: " + id));
        return blogPostMapper.toDetailsDto(blogPost);
    }
    @Override
    @Transactional
    public BlogPostDetailsDto incrementViewCount(Long id) {
        BlogPost blogPost = blogPostRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Blog post not found with id: " + id));

        blogPost.setViewCount(blogPost.getViewCount() + 1);
        blogPost = blogPostRepository.save(blogPost);
        return blogPostMapper.toDetailsDto(blogPost);
    }

    @Override
    @Transactional
    public void deleteBlogPost(Long id) {
        BlogPost blogPost = blogPostRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Blog post not found with id: " + id));
        if (blogPost.getImagePublicId() != null) {
            storageService.deleteImage(blogPost.getImagePublicId());
        }

        blogPostRepository.delete(blogPost);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BlogPostListDto> getBlogPostsByType(String type, Pageable pageable) {
        try {
            BlogPostType blogPostType = BlogPostType.valueOf(type.toUpperCase());
            return blogPostRepository.findByType(blogPostType, pageable)
                    .map(blogPostMapper::toListDto);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid blog post type: " + type);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BlogPostListDto> getMostPopularBlogPosts(Pageable pageable) {
        return blogPostRepository.findAllByOrderByViewCountDesc(pageable)
                .map(blogPostMapper::toListDto);
    }
}