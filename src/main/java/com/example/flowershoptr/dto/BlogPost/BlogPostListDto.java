package com.example.flowershoptr.dto.BlogPost;

import com.example.flowershoptr.enums.BlogPostType;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
@Data
@Builder
public class BlogPostListDto {
    private Long id;
    private String title;
    private String shortDescription;
    private String previewImageUrl;
    private BlogPostType type;
    private Long viewCount;
    private LocalDateTime createdAt;


}