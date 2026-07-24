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

    PageResponse<CourseResponse> search(CourseSearchCriteria criteria, Pageable pageable);

    CourseResponse publish(Long id);

    CourseResponse archive(Long id);

    void delete(Long id);

    CourseStatisticsResponse getStatistics();
}
