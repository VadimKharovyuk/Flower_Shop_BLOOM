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
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
@Slf4j
@Service
@RequiredArgsConstructor
public class BlogPostServiceImpl implements BlogPostService {

    private final BlogPostRepository blogPostRepository;
    private final BlogPostMapper blogPostMapper;
    private final StorageService storageService;

    @CacheEvict(value = {"blogPostsPageList", "popularBlogPostsPageList", "blogPostsByType"}, allEntries = true)
    @Override
    @Transactional
    public BlogPostDetailsDto createBlogPost(BlogPostCreateUpdateDto dto) {
        log.info("Создание нового блогпоста");
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

    @Cacheable(value = "blogPostsPageList", key = "'page:' + #pageable.pageNumber + '-size:' + #pageable.pageSize")
    @Override
    @Transactional(readOnly = true)
    public Page<BlogPostListDto> getBlogPosts(Pageable pageable) {
        log.info("CACHE MISS: Загрузка страницы блогпостов из БД. Page: {}, Size: {}",
                pageable.getPageNumber(), pageable.getPageSize());
        return blogPostRepository.findAll(pageable)
                .map(blogPostMapper::toListDto);
    }

    @Cacheable(value = "blogPostById", key = "#id")
    @Override
    @Transactional(readOnly = true)
    public BlogPostDetailsDto getBlogPostById(Long id) {
        log.info("CACHE MISS: Загрузка блогпоста с ID: {} из БД", id);
        BlogPost blogPost = blogPostRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Blog post not found with id: " + id));
        return blogPostMapper.toDetailsDto(blogPost);
    }

    @CachePut(value = "blogPostById", key = "#id")
    @Override
    @Transactional
    public BlogPostDetailsDto incrementViewCount(Long id) {
        log.info("Инкремент счетчика просмотров для блогпоста с ID: {}", id);
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
    @Cacheable(value = "blogPostsByType", key = "{#type.toUpperCase(), #pageable.pageNumber, #pageable.pageSize}")
    @Override
    @Transactional(readOnly = true)
    public Page<BlogPostListDto> getBlogPostsByType(String type, Pageable pageable) {
        log.info("CACHE MISS: Загрузка страницы блогпостов по типу {} из БД. Page: {}, Size: {}",
                type, pageable.getPageNumber(), pageable.getPageSize());
        try {
            BlogPostType blogPostType = BlogPostType.valueOf(type.toUpperCase());
            return blogPostRepository.findByType(blogPostType, pageable)
                    .map(blogPostMapper::toListDto);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid blog post type: " + type);
        }
    }

    @Cacheable(value = "popularBlogPostsPageList", key = "'page:' + #pageable.pageNumber + '-size:' + #pageable.pageSize")
    @Override
    @Transactional(readOnly = true)
    public Page<BlogPostListDto> getMostPopularBlogPosts(Pageable pageable) {
        log.info("CACHE MISS: Загрузка страницы популярных блогпостов из БД. Page: {}, Size: {}",
                pageable.getPageNumber(), pageable.getPageSize());
        return blogPostRepository.findAllByOrderByViewCountDesc(pageable)
                .map(blogPostMapper::toListDto);
    }

    @Override
    public List<BlogPost> getAllBlogPosts() {
    return   blogPostRepository.findAll();

    }

    @Override
    public List<BlogPost> searchBlogPostsByTitle(String title) {
        return blogPostRepository.findAll().stream()
                .filter(post -> post.getTitle().toLowerCase().contains(title.toLowerCase()))
                .toList();
    }

    @Override
    public Optional<BlogPost> getBlogPostByIdOptional(Long id) {
       return blogPostRepository.findById(id);
    }
}