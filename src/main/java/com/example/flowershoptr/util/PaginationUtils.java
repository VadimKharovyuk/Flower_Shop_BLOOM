package com.example.flowershoptr.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class PaginationUtils {
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 10;
    public static final int MAX_SIZE = 100;



    public Pageable createPageable(int page, int size, String sortBy, boolean ascending) {
        Sort sort = Sort.by(ascending ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        return PageRequest.of(page, size, sort);
    }

    // Метод с дефолтными значениями
    public Pageable createDefaultPageable(String sortBy, boolean ascending) {
        return createPageable(DEFAULT_PAGE, DEFAULT_SIZE, sortBy, ascending);
    }
}