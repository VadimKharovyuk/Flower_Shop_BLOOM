package com.example.flowershoptr.service.serviceImpl;

import com.example.flowershoptr.dto.ShopReview.ShopReviewCreateDTO;
import com.example.flowershoptr.dto.ShopReview.ShopReviewListDTO;
import com.example.flowershoptr.maper.ShopReviewMapper;
import com.example.flowershoptr.model.ShopReview;
import com.example.flowershoptr.repository.ShopReviewRepository;
import com.example.flowershoptr.service.ShopReviewService;
import com.example.flowershoptr.service.StorageService;
import com.example.flowershoptr.util.CloudinaryStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
@RequiredArgsConstructor
@Service
@Slf4j
public class ShopReviewServiceImpl  implements ShopReviewService {
   private final StorageService storageService;
    private final ShopReviewRepository shopReviewRepository ;
    private final ShopReviewMapper shopReviewMapper ;



    @Override
    public ShopReviewCreateDTO createShopReview(ShopReviewCreateDTO shopReviewCreateDTO, MultipartFile imageFile) {
        try {
            // Создаем сущность из DTO
            ShopReview shopReview = shopReviewMapper.toEntity(shopReviewCreateDTO);


            if (imageFile != null && !imageFile.isEmpty()) {
                System.out.println("Файл найден: " + imageFile.getOriginalFilename());

                CloudinaryStorageService.StorageResult storageResult = storageService.uploadImage(imageFile);

                System.out.println("Cloudinary URL: " + storageResult.getUrl());
                System.out.println("Cloudinary ID: " + storageResult.getImageId());

                shopReview.setPreviewImageUrl(storageResult.getUrl());
                shopReview.setImagePublicId(storageResult.getImageId());
            }
            // Установка времени создания
            shopReview.setCreatedAt(LocalDateTime.now());

            // Сохраняем отзыв в БД
            ShopReview savedReview = shopReviewRepository.save(shopReview);

            // Возвращаем DTO с данными созданного отзыва
            return shopReviewMapper.toCreateDTO(savedReview);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при загрузке изображения: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании отзыва: " + e.getMessage(), e);
        }
    }


    @Override
    public List<ShopReviewListDTO> listShopReviews(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        return shopReviewRepository.findAll(pageable)
                .getContent()
                .stream()
                .map(shopReviewMapper::toListDTO)
                .toList();
    }

    @Override
    public Page<ShopReviewListDTO> Pagereview(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ShopReview> reviewPage = shopReviewRepository.findAll(pageable);
        return reviewPage.map(shopReviewMapper::toListDTO);
    }
}
