package com.example.flowershoptr.service;

import com.example.flowershoptr.model.InstagramPost;
import com.example.flowershoptr.repository.InstagramPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InstagramService {
    private final InstagramPostRepository instagramPostRepository;

    public List<InstagramPost> getActivePosts() {
        return instagramPostRepository.findByActiveOrderByDisplayOrderAsc(true);
    }

    public List<InstagramPost> getActivePostsLimited(int limit) {
        return instagramPostRepository.findActivePosts(PageRequest.of(0, limit));
    }

    public InstagramPost savePost(InstagramPost post) {
        return instagramPostRepository.save(post);
    }

    public void deletePost(Long id) {
        instagramPostRepository.deleteById(id);
    }

    public Optional<InstagramPost> getPostById(Long id) {
        return instagramPostRepository.findById(id);
    }
}