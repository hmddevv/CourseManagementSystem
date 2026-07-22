package com.university.coursemanagement.dto.response;

import java.time.LocalDateTime;

/** Chung chi hoan thanh khoa hoc tra ve cho client. */
public record CertificateResponse(
        Long id,
        String code,
        Long enrollmentId,
        Long studentId,
        String studentName,
        Long courseId,
        String courseTitle,
        LocalDateTime issuedAt
) {
}
