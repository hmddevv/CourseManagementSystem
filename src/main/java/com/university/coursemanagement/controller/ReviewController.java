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
@Tag(name = "Reviews", description = "Danh gia khoa hoc va bang xep hang theo diem")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/api/courses/{courseId}/reviews")
    @Operation(summary = "Hoc vien danh gia khoa hoc (1-5 sao), chi khi da ghi danh")
    public ResponseEntity<ApiResponse<ReviewResponse>> create(@PathVariable Long courseId,
                                                              @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Danh gia thanh cong", reviewService.createReview(courseId, request)));
    }

    @GetMapping("/api/courses/{courseId}/reviews")
    @Operation(summary = "Danh sach danh gia cua mot khoa hoc (phan trang)")
    public ApiResponse<PageResponse<ReviewResponse>> getByCourse(
            @PathVariable Long courseId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.ok(reviewService.getByCourse(courseId, pageable));
    }

    @GetMapping("/api/courses/top-rated")
    @Operation(summary = "Bang xep hang khoa hoc theo diem trung binh (phan trang)")
    public ApiResponse<PageResponse<CourseRatingResponse>> topRated(
            @PageableDefault(size = 10) Pageable pageable) {
        return ApiResponse.ok(reviewService.getTopRated(pageable));
    }

    @PutMapping("/api/reviews/{reviewId}")
    @Operation(summary = "Sua danh gia cua chinh minh")
    public ApiResponse<ReviewResponse> update(@PathVariable Long reviewId,
                                              @Valid @RequestBody ReviewRequest request) {
        return ApiResponse.ok("Cap nhat danh gia thanh cong", reviewService.updateReview(reviewId, request));
    }

    @DeleteMapping("/api/reviews/{reviewId}")
    @Operation(summary = "Xoa danh gia")
    public ResponseEntity<Void> delete(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }
}
