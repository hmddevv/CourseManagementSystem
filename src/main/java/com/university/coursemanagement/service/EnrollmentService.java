package com.university.coursemanagement.service;

import com.university.coursemanagement.common.PageResponse;
import com.university.coursemanagement.dto.request.EnrollmentRequest;
import com.university.coursemanagement.dto.request.UpdateProgressRequest;
import com.university.coursemanagement.dto.response.EnrollmentResponse;
import org.springframework.data.domain.Pageable;

public interface EnrollmentService {

    /** Ghi danh hoc vien vao khoa hoc (kiem tra published, suc chua, trung lap). */
    EnrollmentResponse enroll(EnrollmentRequest request);

    /** Huy ghi danh -> giai phong 1 cho. */
    EnrollmentResponse cancel(Long enrollmentId);

    /** Cap nhat tien do; dat 100% -> COMPLETED va ghi completedAt. */
    EnrollmentResponse updateProgress(Long enrollmentId, UpdateProgressRequest request);

    EnrollmentResponse getById(Long enrollmentId);

    PageResponse<EnrollmentResponse> getByStudent(Long studentId, Pageable pageable);

    PageResponse<EnrollmentResponse> getByCourse(Long courseId, Pageable pageable);
}
