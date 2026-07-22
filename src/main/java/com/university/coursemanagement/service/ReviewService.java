package com.university.coursemanagement.service;

import com.university.coursemanagement.common.PageResponse;
import com.university.coursemanagement.dto.request.ReviewRequest;
import com.university.coursemanagement.dto.response.CourseRatingResponse;
import com.university.coursemanagement.dto.response.ReviewResponse;
import org.springframework.data.domain.Pageable;

/** Danh gia khoa hoc va xep hang theo diem trung binh. */
public interface ReviewService {

    /** Hoc vien danh gia khoa hoc. Chi cho phep khi da ghi danh va chua danh gia lan nao. */
    ReviewResponse createReview(Long courseId, ReviewRequest request);

    /** Cap nhat danh gia da co cua chinh hoc vien do. */
    ReviewResponse updateReview(Long reviewId, ReviewRequest request);

    void deleteReview(Long reviewId);

    /** Danh sach danh gia cua mot khoa hoc, co phan trang. */
    PageResponse<ReviewResponse> getByCourse(Long courseId, Pageable pageable);

    /** Bang xep hang khoa hoc theo diem trung binh, co phan trang. */
    PageResponse<CourseRatingResponse> getTopRated(Pageable pageable);
}
