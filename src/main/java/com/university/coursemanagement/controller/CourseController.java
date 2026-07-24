package com.university.coursemanagement.controller;

import com.university.coursemanagement.common.ApiResponse;
import com.university.coursemanagement.common.PageResponse;
import com.university.coursemanagement.dto.request.CourseRequest;
import com.university.coursemanagement.dto.request.CourseSearchCriteria;
import com.university.coursemanagement.dto.response.CourseResponse;
import com.university.coursemanagement.dto.response.CourseStatisticsResponse;
import com.university.coursemanagement.entity.enums.CourseLevel;
import com.university.coursemanagement.entity.enums.CourseStatus;
import com.university.coursemanagement.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/courses")
@Tag(name = "Courses", description = "Quản lý khóa học: CRUD, tìm kiếm, xuất bản, thống kê")
public class CourseController {
    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
    @Operation(summary = "Tạo khóa học mới (trạng thái mặc định DRAFT)")
    public ResponseEntity<ApiResponse<CourseResponse>> create(@Valid @RequestBody CourseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Tạo khóa học thành công", courseService.create(request)));
    }

    @GetMapping
    @Operation(summary = "Tìm kiếm + lọc + phân trang + sắp xếp khóa học",
            description = "Ví dụ sort: ?sort=price,desc&sort=title,asc. Các tham số lọc đều tùy chọn.")
    public ApiResponse<PageResponse<CourseResponse>> search(
            @Parameter(description = "Từ khóa trong tiêu đề/mô tả") @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long instructorId,
            @RequestParam(required = false) CourseLevel level,
            @RequestParam(required = false) CourseStatus status,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        CourseSearchCriteria criteria = new CourseSearchCriteria(
                keyword, categoryId, instructorId, level, status, minPrice, maxPrice);
        return ApiResponse.ok(courseService.search(criteria, pageable));
    }

    @GetMapping("/statistics")
    @Operation(summary = "Thống kê tổng hợp cho dashboard")
    public ApiResponse<CourseStatisticsResponse> statistics() {
        return ApiResponse.ok(courseService.getStatistics());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Chi tiết khóa học")
    public ApiResponse<CourseResponse> getById(@PathVariable Long id) {
        return ApiResponse.ok(courseService.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật khóa học")
    public ApiResponse<CourseResponse> update(@PathVariable Long id,
                                              @Valid @RequestBody CourseRequest request) {
        return ApiResponse.ok("Cập nhật thành công", courseService.update(id, request));
    }

    @PatchMapping("/{id}/publish")
    @Operation(summary = "Xuất bản khóa học (DRAFT -> PUBLISHED, cần >= 1 bài học)")
    public ApiResponse<CourseResponse> publish(@PathVariable Long id) {
        return ApiResponse.ok("Xuất bản thành công", courseService.publish(id));
    }

    @PatchMapping("/{id}/archive")
    @Operation(summary = "Lưu trữ khóa học (-> ARCHIVED)")
    public ApiResponse<CourseResponse> archive(@PathVariable Long id) {
        return ApiResponse.ok("Lưu trữ thành công", courseService.archive(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa khóa học (chỉ khi không có học viên đang học)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        courseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
