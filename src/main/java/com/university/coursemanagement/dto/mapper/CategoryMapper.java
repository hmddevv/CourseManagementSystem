package com.university.coursemanagement.dto.mapper;

import com.university.coursemanagement.dto.request.CategoryRequest;
import com.university.coursemanagement.dto.response.CategoryResponse;
import com.university.coursemanagement.entity.Category;
import org.springframework.stereotype.Component;

/** Chuyen doi giua Category entity va cac DTO. Giu Entity tach biet khoi API. */
@Component
public class CategoryMapper {

    public Category toEntity(CategoryRequest request) {
        return Category.builder()
                .name(request.name())
                .description(request.description())
                .build();
    }

    public void updateEntity(Category category, CategoryRequest request) {
        category.setName(request.name());
        category.setDescription(request.description());
    }

    public CategoryResponse toResponse(Category category, long courseCount) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                (int) courseCount,
                category.getCreatedAt()
        );
    }
}
