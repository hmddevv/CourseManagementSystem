package com.university.coursemanagement.service;

import com.university.coursemanagement.common.PageResponse;
import com.university.coursemanagement.dto.request.ReviewRequest;
import com.university.coursemanagement.dto.response.CourseRatingResponse;
import com.university.coursemanagement.dto.response.ReviewResponse;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    ReviewResponse createReview(Long courseId, ReviewRequest request);

    ReviewResponse updateReview(Long reviewId, ReviewRequest request);

    void deleteReview(Long reviewId);

    PageResponse<ReviewResponse> getByCourse(Long courseId, Pageable pageable);

    PageResponse<CourseRatingResponse> getTopRated(Pageable pageable);
}
