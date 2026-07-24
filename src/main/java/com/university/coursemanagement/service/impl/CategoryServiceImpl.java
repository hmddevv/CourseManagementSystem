package com.university.coursemanagement.service.impl;

import com.university.coursemanagement.common.PageResponse;
import com.university.coursemanagement.config.CacheConfig;
import com.university.coursemanagement.dto.mapper.CategoryMapper;
import com.university.coursemanagement.dto.request.CategoryRequest;
import com.university.coursemanagement.dto.response.CategoryResponse;
import com.university.coursemanagement.entity.Category;
import com.university.coursemanagement.exception.BusinessException;
import com.university.coursemanagement.exception.DuplicateResourceException;
import com.university.coursemanagement.exception.ResourceNotFoundException;
import com.university.coursemanagement.repository.CategoryRepository;
import com.university.coursemanagement.repository.CourseRepository;
import com.university.coursemanagement.service.CategoryService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CourseRepository courseRepository;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository,
                               CourseRepository courseRepository,
                               CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.courseRepository = courseRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheConfig.CATEGORIES_CACHE, allEntries = true)
    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.name())) {
            throw new DuplicateResourceException("Danh mục '%s' đã tồn tại".formatted(request.name()));
        }
        Category saved = categoryRepository.save(categoryMapper.toEntity(request));
        return categoryMapper.toResponse(saved, 0);
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheConfig.CATEGORIES_CACHE, allEntries = true)
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = findOrThrow(id);
        categoryRepository.findByNameIgnoreCase(request.name())
                .filter(other -> !other.getId().equals(id))
                .ifPresent(other -> {
                    throw new DuplicateResourceException("Danh mục '%s' đã tồn tại".formatted(request.name()));
                });
        categoryMapper.updateEntity(category, request);
        long count = courseRepository.countByCategoryId(id);
        return categoryMapper.toResponse(category, count);
    }

    @Override
    public CategoryResponse getById(Long id) {
        Category category = findOrThrow(id);
        return categoryMapper.toResponse(category, courseRepository.countByCategoryId(id));
    }

    @Override
    public PageResponse<CategoryResponse> getAll(Pageable pageable) {
        return PageResponse.from(
                categoryRepository.findAll(pageable)
                        .map(c -> categoryMapper.toResponse(c, courseRepository.countByCategoryId(c.getId())))
        );
    }

    @Override
    @Cacheable(CacheConfig.CATEGORIES_CACHE)
    public List<CategoryResponse> getAllSimple() {
        return categoryRepository.findAll().stream()
                .map(c -> categoryMapper.toResponse(c, courseRepository.countByCategoryId(c.getId())))
                .toList();
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheConfig.CATEGORIES_CACHE, allEntries = true)
    public void delete(Long id) {
        Category category = findOrThrow(id);
        if (courseRepository.existsByCategoryId(id)) {
            throw new BusinessException("Không thể xóa danh mục đang có khóa học. Hãy chuyển/xóa khóa học trước.");
        }
        categoryRepository.delete(category);
    }

    private Category findOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("danh mục", id));
    }
}
