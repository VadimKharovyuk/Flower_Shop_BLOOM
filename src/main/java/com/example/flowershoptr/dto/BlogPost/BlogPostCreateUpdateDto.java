package com.example.flowershoptr.dto.BlogPost;

import com.example.flowershoptr.enums.BlogPostType;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BlogPostCreateUpdateDto {
    private String title;
    private String shortDescription;
    private String fullContent;
    private BlogPostType type;
    private String previewImageUrl;
    private String imagePublicId;
}
