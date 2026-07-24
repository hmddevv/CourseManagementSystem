package com.university.coursemanagement.service;

import com.university.coursemanagement.common.PageResponse;
import com.university.coursemanagement.dto.request.EnrollmentRequest;
import com.university.coursemanagement.dto.request.UpdateProgressRequest;
import com.university.coursemanagement.dto.response.EnrollmentResponse;
import org.springframework.data.domain.Pageable;

public interface EnrollmentService {
    EnrollmentResponse enroll(EnrollmentRequest request);

    EnrollmentResponse cancel(Long enrollmentId);

    EnrollmentResponse updateProgress(Long enrollmentId, UpdateProgressRequest request);

    EnrollmentResponse getById(Long enrollmentId);

    PageResponse<EnrollmentResponse> getByStudent(Long studentId, Pageable pageable);

    PageResponse<EnrollmentResponse> getByCourse(Long courseId, Pageable pageable);
}
