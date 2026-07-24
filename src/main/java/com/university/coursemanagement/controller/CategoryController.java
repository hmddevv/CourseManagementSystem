package com.university.coursemanagement.controller;

import com.university.coursemanagement.common.ApiResponse;
import com.university.coursemanagement.common.PageResponse;
import com.university.coursemanagement.dto.request.CategoryRequest;
import com.university.coursemanagement.dto.response.CategoryResponse;
import com.university.coursemanagement.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Categories", description = "Quản lý danh mục khóa học")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    @Operation(summary = "Tạo danh mục mới")
    public ResponseEntity<ApiResponse<CategoryResponse>> create(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse created = categoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Tạo danh mục thành công", created));
    }

    @GetMapping
    @Operation(summary = "Danh sách danh mục (phân trang)")
    public ApiResponse<PageResponse<CategoryResponse>> getAll(
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return ApiResponse.ok(categoryService.getAll(pageable));
    }

    @GetMapping("/all")
    @Operation(summary = "Danh sách danh mục gọn (cho dropdown)")
    public ApiResponse<List<CategoryResponse>> getAllSimple() {
        return ApiResponse.ok(categoryService.getAllSimple());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Chi tiết danh mục")
    public ApiResponse<CategoryResponse> getById(@PathVariable Long id) {
        return ApiResponse.ok(categoryService.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật danh mục")
    public ApiResponse<CategoryResponse> update(@PathVariable Long id,
                                                @Valid @RequestBody CategoryRequest request) {
        return ApiResponse.ok("Cập nhật thành công", categoryService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa danh mục (chỉ khi không có khóa học)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
