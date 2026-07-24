package com.university.coursemanagement.controller;

import com.university.coursemanagement.common.ApiResponse;
import com.university.coursemanagement.common.PageResponse;
import com.university.coursemanagement.dto.request.ReviewRequest;
import com.university.coursemanagement.dto.response.CourseRatingResponse;
import com.university.coursemanagement.dto.response.ReviewResponse;
import com.university.coursemanagement.service.ReviewService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Reviews", description = "Đánh giá khóa học và bảng xếp hạng theo điểm")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/api/courses/{courseId}/reviews")
    @Operation(summary = "Học viên đánh giá khóa học (1-5 sao), chỉ khi đã ghi danh")
    public ResponseEntity<ApiResponse<ReviewResponse>> create(@PathVariable Long courseId,
                                                              @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Đánh giá thành công", reviewService.createReview(courseId, request)));
    }

    @GetMapping("/api/courses/{courseId}/reviews")
    @Operation(summary = "Danh sách đánh giá của một khóa học (phân trang)")
    public ApiResponse<PageResponse<ReviewResponse>> getByCourse(
            @PathVariable Long courseId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.ok(reviewService.getByCourse(courseId, pageable));
    }

    @GetMapping("/api/courses/top-rated")
    @Operation(summary = "Bảng xếp hạng khóa học theo điểm trung bình (phân trang)")
    public ApiResponse<PageResponse<CourseRatingResponse>> topRated(
            @PageableDefault(size = 10) Pageable pageable) {
        return ApiResponse.ok(reviewService.getTopRated(pageable));
    }

    @PutMapping("/api/reviews/{reviewId}")
    @Operation(summary = "Sửa đánh giá của chính mình")
    public ApiResponse<ReviewResponse> update(@PathVariable Long reviewId,
                                              @Valid @RequestBody ReviewRequest request) {
        return ApiResponse.ok("Cập nhật đánh giá thành công", reviewService.updateReview(reviewId, request));
    }

    @DeleteMapping("/api/reviews/{reviewId}")
    @Operation(summary = "Xóa đánh giá")
    public ResponseEntity<Void> delete(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }
}
