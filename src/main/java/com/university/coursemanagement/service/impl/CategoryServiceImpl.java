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

/**
 * Nghiep vu danh muc. Cac phuong thuc ghi duoc bao boi @Transactional;
 * phuong thuc doc dung readOnly de toi uu.
 */
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
            throw new DuplicateResourceException("Danh muc '%s' da ton tai".formatted(request.name()));
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
                    throw new DuplicateResourceException("Danh muc '%s' da ton tai".formatted(request.name()));
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

    /**
     * Danh sach danh muc rut gon - duoc goi o moi man hinh co dropdown nen cache lai.
     * Cache bi xoa toan bo moi khi co thao tac ghi len danh muc, nen du lieu khong the cu.
     */
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
            throw new BusinessException("Khong the xoa danh muc dang co khoa hoc. Hay chuyen/xoa khoa hoc truoc.");
        }
        categoryRepository.delete(category);
    }

    private Category findOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("danh muc", id));
    }
}
