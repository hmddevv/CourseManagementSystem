package com.university.coursemanagement.service;

import com.university.coursemanagement.common.PageResponse;
import com.university.coursemanagement.dto.request.CourseRequest;
import com.university.coursemanagement.dto.request.CourseSearchCriteria;
import com.university.coursemanagement.dto.response.CourseResponse;
import com.university.coursemanagement.dto.response.CourseStatisticsResponse;
import org.springframework.data.domain.Pageable;

public interface CourseService {

    CourseResponse create(CourseRequest request);

    CourseResponse update(Long id, CourseRequest request);

    CourseResponse getById(Long id);

    /** Tim kiem + loc + phan trang + sap xep. */
    PageResponse<CourseResponse> search(CourseSearchCriteria criteria, Pageable pageable);

    /** Xuat ban khoa hoc (DRAFT -> PUBLISHED). Yeu cau co it nhat 1 bai hoc. */
    CourseResponse publish(Long id);

    /** Luu tru khoa hoc (-> ARCHIVED). */
    CourseResponse archive(Long id);

    void delete(Long id);

    /** Thong ke tong hop cho dashboard. */
    CourseStatisticsResponse getStatistics();
}
