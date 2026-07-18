package com.university.coursemanagement.service;

import com.university.coursemanagement.common.PageResponse;
import com.university.coursemanagement.dto.request.CategoryRequest;
import com.university.coursemanagement.dto.response.CategoryResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {

    CategoryResponse create(CategoryRequest request);

    CategoryResponse update(Long id, CategoryRequest request);

    CategoryResponse getById(Long id);

    PageResponse<CategoryResponse> getAll(Pageable pageable);

    /** Danh sach gon (khong phan trang) - phuc vu dropdown o frontend. */
    List<CategoryResponse> getAllSimple();

    void delete(Long id);
}
