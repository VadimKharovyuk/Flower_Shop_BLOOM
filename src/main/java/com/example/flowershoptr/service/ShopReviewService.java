package com.example.flowershoptr.service;

import com.example.flowershoptr.dto.ShopReview.ShopReviewCreateDTO;
import com.example.flowershoptr.dto.ShopReview.ShopReviewListDTO;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ShopReviewService {


    ShopReviewCreateDTO createShopReview(ShopReviewCreateDTO shopReviewCreateDTO , MultipartFile imageFile);

    List<ShopReviewListDTO> listShopReviews( int limit);

    Page<ShopReviewListDTO> Pagereview(int page, int size);
}
