package com.university.coursemanagement.dto.response;

import java.time.LocalDateTime;

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
